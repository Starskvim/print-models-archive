package com.starskvim.printmodelsarchive.rest.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class ArchiveAdminController(

) {

    @GetMapping
    suspend fun getAdminPage(): String = "admin"

}