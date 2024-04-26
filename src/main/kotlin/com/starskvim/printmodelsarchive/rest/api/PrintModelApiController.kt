package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.domain.PrintModelService
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import com.starskvim.printmodelsarchive.utils.PageUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/models")
class PrintModelApiController(
    private val service: PrintModelService
) {

    @GetMapping
    suspend fun getModels(
        requestPageable: Pageable?,
        @RequestParam("wordName", required = false) wordName: String?,
        @RequestParam("wordCategory", required = false) category: String?,
        @RequestParam("rate", required = false) rate: Int?
    ): Page<PrintModel> {
        val pageable = PageUtils.getRequestPageable(requestPageable)
        return service.getPrintModelsPage(PrintModelSearchParams(wordName, category, rate), pageable)
    }

    @GetMapping("/model")
    suspend fun getModel(
        @RequestParam("modelId", required = false) modelId: String
    ): PrintModel {
        return service.getPrintModelById(modelId)
    }
}