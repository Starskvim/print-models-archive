package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.domain.create.CreatePrintModelService
import com.starskvim.printmodelsarchive.utils.Constants.CREATE_ARCHIVE
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/service")
class AdminApiController(

    private val createService: CreatePrintModelService

) {

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(CREATE_ARCHIVE)
    suspend fun createArchive() {
        createService.initializeArchive()
    }
}