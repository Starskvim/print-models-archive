package com.starskvim.print.models.archive.domain.meta.openrouter

import com.starskvim.print.models.archive.config.ai.OpenRouterConfiguration
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiImageTagService
import com.starskvim.print.models.archive.utils.Constants.Prompt.TAGGING_PROMPT
import com.starskvim.print.models.archive.utils.Constants.Prompt.TAGGING_PROMPT_W_F_N
import com.starskvim.print.models.archive.utils.MetaUtils.splitTags
import mu.KLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.util.*

@Service
class OpenRouterService(
    @Qualifier("openRouterWebClient")
    private val client: WebClient,
    private val config: OpenRouterConfiguration,
    private val geminiImageTagService: GeminiImageTagService
) {

    suspend fun generateTags(
        imagePathString: String
    ): List<String> {
        val p = geminiImageTagService.readAndValidateLocalImage(imagePathString) // todo
        return generateTags(images = listOf(p.first), model = config.model)
            .choices
            ?.first()
            ?.message
            ?.content
            .splitTags()
    }

    suspend fun generateTags(
        images: List<ByteArray>,
        modelName: String? = null,
        model: String = "openai/gpt-4.1-nano"
    ): ChatCompletionResponse {
        val prompt = when (modelName) {
            null -> TAGGING_PROMPT
            else -> TAGGING_PROMPT_W_F_N + modelName
        }
        val content: MutableList<Content> = mutableListOf(
            TextContent(prompt)
        )
        content += images.map { bytes ->
            val b64 = Base64.getEncoder().encodeToString(bytes)
            ImageUrlContent(ImageUrl("data:image/jpeg;base64," + b64))
        }
        val req = ChatCompletionRequest(
            model = model,
            messages = listOf(Message(role = "user", content = content))
        )
        return client.post()
            .uri("/chat/completions")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(req))
            .retrieve()
            .awaitBody<ChatCompletionResponse>()
    }

    companion object {
        val log = KLogging().logger()
    }
}