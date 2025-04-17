package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.context.PrintModelLocalContextService
import com.starskvim.print.models.archive.domain.job.LocalContextJobService
import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiImageTagService
import com.starskvim.print.models.archive.domain.meta.openrouter.OpenRouterService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestApiController(
    private val taggingService: GeminiImageTagService,
    private val imageMetaService: ImageMetaService,
    private val localContextJobService: LocalContextJobService,
    private val localContextService: PrintModelLocalContextService,
    private val openRouterService: OpenRouterService
) {

    @PostMapping("/tags")
    suspend fun testTag(
        @RequestBody request: Request,
    ) : List<String> {
        return taggingService.generateTags(request.path, request.name);
    }

    @PostMapping("/tags/open")
    suspend fun testTagOpen(
        @RequestBody request: Request,
    ): List<String>? {
        return openRouterService.generateTags(request.path);
    }

    @PostMapping("/tags/{id}")
    suspend fun testModelTag(
        @PathVariable id: String,
    ) {
        imageMetaService.createMetaById(id)
    }

    @PostMapping("/context/{id}")
    suspend fun testModelContext(
        @PathVariable id: String,
    ) {
        localContextJobService.process(id)
    }

    @PostMapping("/context/load")
    suspend fun testModelContextGet(
        @RequestBody request: Request,
    ) {
        var r = localContextService.loadContext(request.path)
        println()
    }

    @PostMapping("/context")
    suspend fun testModelContexts(
    ) {
        localContextJobService.process()
    }

    class Request(
        val path: String,
        val name: String?
    )
}