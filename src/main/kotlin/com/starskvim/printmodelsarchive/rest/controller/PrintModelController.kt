package com.starskvim.printmodelsarchive.rest.controller

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/models")
class PrintModelController(

) {
    @GetMapping("/")
    fun getModels(
        model: Model,
        pageable: Pageable,
        @RequestParam("name") wordName: String,
        @RequestParam("category") category: String,
    ) {

    }
}