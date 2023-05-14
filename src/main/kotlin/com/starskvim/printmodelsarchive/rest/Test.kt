package com.starskvim.printmodelsarchive.rest

import com.starskvim.printmodelsarchive.persistance.MinioDataService
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class Test(

    private val dataService: PrintModelDataService,
    private val minioDataService: MinioDataService

) {

    @PostMapping("/post")
    suspend fun testPost(@RequestBody model: PrintModelData): PrintModelData? {
        return dataService.savePrintModel(model)
    }

    @PostMapping("/postS3")
    suspend fun testS3(@RequestBody test: TestPath) {
        minioDataService.saveObject(test.path)
    }

    @GetMapping("/postS3")
    suspend fun getTestS3(@RequestParam test: String): String {
        return minioDataService.getPrintModelImageUrl(test)
    }
}