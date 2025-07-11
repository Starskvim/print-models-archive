package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.print_model.PrintModelFavoritesService
import com.starskvim.print.models.archive.domain.print_model.UserFavoritesService
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModel
import com.starskvim.print.models.archive.utils.PageUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// TODO replace page
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

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @DeleteMapping("/{name}")
    suspend fun removeModel(
        @PathVariable name: String
    ) {
        userService.remove(name)
    }
}
