package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.utils.Constants.CREATE_ARCHIVE
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/service")
class AdminApiController {

    @PostMapping(CREATE_ARCHIVE)
    fun createArchive() {

    }

}