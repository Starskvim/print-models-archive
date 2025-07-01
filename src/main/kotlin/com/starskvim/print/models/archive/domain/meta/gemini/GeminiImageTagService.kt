package com.starskvim.print.models.archive.domain.meta.gemini

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.utils.Constants.Prompt.TAGGING_PROMPT
import com.starskvim.print.models.archive.utils.Constants.Prompt.TAGGING_PROMPT_W_F_N
import com.starskvim.print.models.archive.utils.MetaUtils.splitTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.util.*

@Service
class GeminiImageTagService(
    @Qualifier("geminiWebClient")
    private val geminiWebClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val config: GeminiClientConfiguration
) {

    suspend fun generateTags(
        imagePathString: String,
        modelName: String? = null
    ): List<String> { // todo
        val (imageBytes, mimeType) = readAndValidateLocalImage(imagePathString)
        val base64ImageData = Base64.getEncoder().encodeToString(imageBytes)
        val requestBody = buildRequestBodyMap(mimeType, base64ImageData, modelName)
        return request(requestBody, imagePathString)
    }

    suspend fun request(requestBody: Map<String, Any>,
                        imagePathString: String) : List<String> {
        val aiModel = config.getFirstAvailableModel() ?: throw GeminiLimitRequestException("All models limited")
        try {
            val responseBody = geminiWebClient.post()
                .uri("/{modelName}:generateContent?key={apiKey}", aiModel.model, config.apikey)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .awaitBody<String>() // todo
            log.debug("Received successful response body from Gemini API.")

            val parsed = parseSuccessResponse(responseBody)
            aiModel.incrementRequest()
            return parsed

        } catch (e: WebClientResponseException) {
            if (e.statusCode.value() == 429) { // 429 TOO_MANY_REQUESTS = RETRY
                val statusCode = e.statusCode
                val errorBody = e.responseBodyAsString
                log.error("Gemini API request failed: Status {}, Body: {}", statusCode, errorBody, e)
                val message = extractErrorMessage(errorBody, "API request failed with status $statusCode")
                throw GeminiApiException("Gemini API error: ($statusCode) $message", e, statusCode)
            } else if (e.statusCode.value() == 500) { // TODO LIMIT CODE? = next ai model
                log.error{ "Gemini API request failed LIMIT, retry with new model" }
                return request(requestBody, imagePathString)
            }
            throw e
        } catch (e: GeminiLimitRequestException) {
            throw e // return
        } catch (e: Exception) {
            log.error("Unexpected error during tag generation for $imagePathString", e)
            throw RuntimeException("Unexpected error generating tags: ${e.message}", e)
        }
    }

    suspend fun readAndValidateLocalImage(imagePathString: String): Pair<ByteArray, String> =
        withContext(Dispatchers.IO) {
            val imagePath = try {
                Paths.get(imagePathString).normalize()
            } catch (e: InvalidPathException) {
                throw e
            }

            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                throw NoSuchFileException(imagePath.toString())
            }

            log.debug("Reading bytes from file: {}", imagePath)
            val imageBytes = Files.readAllBytes(imagePath)
            log.debug("Read {} bytes from file.", imageBytes.size)

            val mimeType = Files.probeContentType(imagePath)?.lowercase()
                ?: run {
                    log.warn { "Could not determine MIME type for path: $imagePath. Attempting default." }
                    "application/octet-stream"
                }

            if (mimeType !in SUPPORTED_MIME_TYPES) {
                val errorMsg =
                    "Unsupported image MIME type detected for file ${imagePath}: $mimeType. Supported: $SUPPORTED_MIME_TYPES"
                log.error(errorMsg)
                throw IllegalArgumentException(errorMsg)
            }
            log.debug("Detected MIME type: {}", mimeType)

            imageBytes to mimeType
        }

    private fun buildRequestBodyMap(
        mimeType: String,
        base64ImageData: String,
        modelName: String?
    ): Map<String, Any> {
        val prompt = when (modelName) {
            null -> TAGGING_PROMPT
            else -> TAGGING_PROMPT_W_F_N + modelName
        }
        val inlineData = mapOf("mimeType" to mimeType, "data" to base64ImageData)
        val textPart = mapOf("text" to prompt)
        val imagePart = mapOf("inlineData" to inlineData)
        val parts = listOf(textPart, imagePart)
        val content = mapOf("parts" to parts)
        return mapOf("contents" to listOf(content))
    }

    private fun parseSuccessResponse(responseBody: String): List<String> {
        log.debug("Parsing successful response body...")
        val rootNode: JsonNode = try {
            objectMapper.readTree(responseBody)
        } catch (e: JsonProcessingException) {
            log.error("Failed to parse JSON response body: {}", responseBody, e)
            throw e
        }

        val candidates = rootNode.path("candidates")
        if (!candidates.isArray || candidates.isEmpty) {
            val blockReason = rootNode.path("promptFeedback")
                ?.path("blockReason")
                ?.asText("NONE")
                ?: "NONE"
            if (blockReason != "NONE" && blockReason != "BLOCK_REASON_UNSPECIFIED") {
                log.warn { "Prompt was blocked. Reason:  $blockReason" }
            } else {
                log.warn("No 'candidates' found in the successful response.")
            }
            return emptyList()
        }

        val firstCandidate = candidates[0]
        val finishReason = firstCandidate?.path("finishReason")?.asText()?.uppercase()

        if (finishReason != "STOP") {
            val blockReasonStr = firstCandidate?.path("finishReasonDetails")
                ?.path("blockReason")
                ?.asText("UNKNOWN")
                ?: "UNKNOWN"
            log.warn { "Gemini generation finished with reason: $finishReason. Block Reason: $blockReasonStr" }
            if (finishReason == "SAFETY") {
                log.warn("Generation blocked due to safety settings.")
            }
            return emptyList()
        }

        val textPart = firstCandidate.path("content")
            ?.path("parts")
            ?.firstOrNull { it.has("text") }
        val rawText = textPart?.path("text")
            ?.asText()

        if (rawText == null) {
            log.warn("No text part found in the candidate's content.")
            return emptyList()
        }

        log.debug { "Raw text extracted from Gemini response: $rawText" }
        val tags = rawText.splitTags()
        log.info("Successfully parsed {} tags.", tags.size)
        return tags
    }

    private fun extractErrorMessage(errorBody: String?, defaultMessage: String): String {
        if (errorBody.isNullOrBlank()) return defaultMessage
        return try {
            objectMapper.readTree(errorBody)
                ?.path("error")
                ?.path("message")
                ?.asText(defaultMessage)
                ?: defaultMessage
        } catch (e: JsonProcessingException) {
            log.warn { "Could not parse error response body: $errorBody" }
            defaultMessage
        }
    }

    companion object {
        val log = KLogging().logger()
        private val SUPPORTED_MIME_TYPES =
            listOf("image/png", "image/jpeg", "image/webp", "image/heic", "image/heif")
    }
}

