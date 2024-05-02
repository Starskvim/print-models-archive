package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.PrintModelService
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.rest.model.response.PrintModelCardsResponse
import com.starskvim.print.models.archive.rest.model.response.PrintModelResponse
import com.starskvim.print.models.archive.utils.PageUtils
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

// TODO short api model
@RestController
@RequestMapping("/api/models")
class PrintModelApiController(
    private val service: PrintModelService
) {

    @GetMapping
    suspend fun getModels(
        requestPageable: Pageable?, // replace
        @RequestParam("name", required = false) name: String?,
        @RequestParam("category", required = false) category: String?,
        @RequestParam("rate", required = false) rate: Int?
    ): PrintModelCardsResponse {
        val pageable = PageUtils.getRequestPageable(requestPageable)
        return service.getPrintModelsPageApi(PrintModelSearchParams(name, category, rate), pageable)
    }

    @GetMapping("/{id}")
    suspend fun getModel(
        @PathVariable("id") id: String
    ): PrintModelResponse {
        return PrintModelResponse(service.getPrintModelById(id))
    }
}