package com.starskvim.print.models.archive.domain.meta.openrouter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: List<Content>
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = TextContent::class, name = "text"),
    JsonSubTypes.Type(value = ImageUrlContent::class, name = "image_url")
)
sealed class Content

@JsonTypeName("text")
data class TextContent(val text: String) : Content()

@JsonTypeName("image_url")
data class ImageUrlContent(
    @JsonProperty("image_url") val image: ImageUrl
) : Content()

data class ImageUrl(val url: String)

data class ChatCompletionResponse(
    val id: String?,
    @JsonProperty("object") val obj: String?,
    val created: Long?,
    val model: String?,
    val usage: Usage?,
    val choices: List<Choice>?
)

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)

data class Choice(
    val index: Int?,
    val message: ResponseMessage?,
    @JsonProperty("finish_reason") val finishReason: String?
)

data class ResponseMessage(
    val content: String
)