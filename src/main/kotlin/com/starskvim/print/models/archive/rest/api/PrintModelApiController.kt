package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.PrintModelService
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.rest.model.response.PrintModelCardsResponse
import com.starskvim.print.models.archive.rest.model.response.PrintModelResponse
import com.starskvim.print.models.archive.utils.PageUtils
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// TODO short api model
@RestController
@RequestMapping("/api/models")
class PrintModelApiController(
    private val service: PrintModelService
) {

    @GetMapping
    suspend fun getModels(
        requestPageable: Pageable?, // replace
        @RequestParam("wordName", required = false) wordName: String?,
        @RequestParam("wordCategory", required = false) category: String?,
        @RequestParam("rate", required = false) rate: Int?
    ): PrintModelCardsResponse {
        val pageable = PageUtils.getRequestPageable(requestPageable)
        return service.getPrintModelsPageApi(PrintModelSearchParams(wordName, category, rate), pageable)
    }

    @GetMapping("/model")
    suspend fun getModel(
        @RequestParam("modelId", required = false) modelId: String
    ): PrintModelResponse {
        return PrintModelResponse(service.getPrintModelById(modelId))
    }
}