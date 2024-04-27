package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.CategoriesInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/catalog")
class CatalogApiController(
    private val categoriesService: CategoriesInfoService
) {

    @GetMapping
    suspend fun getCatalog() = categoriesService.getCatalog()
}