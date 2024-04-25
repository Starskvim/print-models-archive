package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.domain.PrintModelFavoritesService
import com.starskvim.printmodelsarchive.domain.UserFavoritesService
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import com.starskvim.printmodelsarchive.utils.PageUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/models/favorites")
class PrintModelFavoritesApiController(
    private val modelService: PrintModelFavoritesService,
    private val userService: UserFavoritesService
) {

    @GetMapping
    suspend fun getFavorites(
        requestPageable: Pageable?,
    ): Page<PrintModel> {
        val pageable = PageUtils.getRequestPageable(requestPageable)
        return modelService.getPrintModelFavoritesPage(pageable)
    }

    // TODO id not appropriate
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @PostMapping("/{name}")
    suspend fun addModel(
        @PathVariable name: String,
    ) {
        userService.add(name)
    }
}