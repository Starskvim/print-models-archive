package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.utils.Constants.CREATE_ARCHIVE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/service")
class AdminApiController {

    @PostMapping(CREATE_ARCHIVE)
    suspend fun createArchive() {

    }
}