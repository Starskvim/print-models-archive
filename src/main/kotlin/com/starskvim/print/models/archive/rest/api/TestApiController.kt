package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.domain.meta.ImageTaggingService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestApiController(
    private val taggingService: ImageTaggingService,
    private val imageMetaService: ImageMetaService
) {

    @PostMapping("/tags")
    suspend fun testTag(
        @RequestBody request: Request,
    ) : List<String> {
        return taggingService.generateTags(request.path);
    }

    @PostMapping("/tags/{id}")
    suspend fun testModelTag(
        @PathVariable id: String,
    ) {
        imageMetaService.createMetaById(id)
    }

    class Request(
        val path: String
    )
}