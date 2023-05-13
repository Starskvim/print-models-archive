package com.starskvim.printmodelsarchive.rest

import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class Test(

    private val dataService: PrintModelDataService

) {

    @PostMapping("/post")
    suspend fun testPost(@RequestBody model : PrintModelData) : PrintModelData? {
        return dataService.savePrintModel(model)
    }
}