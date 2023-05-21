package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.domain.PrintModelService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/models")
class PrintModelApiController(

    private val service: PrintModelService

) {

}