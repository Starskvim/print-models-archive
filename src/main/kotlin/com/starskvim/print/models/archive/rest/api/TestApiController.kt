package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.context.PrintModelLocalContextService
import com.starskvim.print.models.archive.domain.job.LocalContextJobService
import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.domain.meta.ImageTagService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestApiController(
    private val taggingService: ImageTagService,
    private val imageMetaService: ImageMetaService,
    private val localContextJobService: LocalContextJobService,
    private val localContextService: PrintModelLocalContextService
) {

    @PostMapping("/tags")
    suspend fun testTag(
        @RequestBody request: Request,
    ) : List<String> {
        return taggingService.generateTags(request.path, request.name);
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