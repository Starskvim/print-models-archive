package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.print_model.PrintModelService
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.rest.model.response.PrintModelCardsResponse
import com.starskvim.print.models.archive.rest.model.response.PrintModelResponse
import com.starskvim.print.models.archive.rest.model.response.SuggestionsResponse
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
        @RequestParam("rate", required = false) rate: Int?,
        @RequestParam("nsfwOnly", required = false) nsfwOnly: Boolean?
    ): PrintModelCardsResponse {
        val pageable = PageUtils.getRequestPageable(requestPageable)
        return service.getPrintModelsPageApi(
            PrintModelSearchParams(
                modelName = name,
                category = category,
                rate = rate,
                nsfwOnly = nsfwOnly
            ),
            pageable
        )
    }

    // TODO by name in url?
    @GetMapping("/{id}")
    suspend fun getModel(
        @PathVariable("id") id: String
    ): PrintModelResponse {
        return PrintModelResponse(service.getPrintModelById(id))
    }

    @GetMapping("/suggestions/{query}")
    suspend fun getSuggestionsModels(
        @PathVariable("query") query: String,
    ): SuggestionsResponse {
        return SuggestionsResponse(service.getSuggestionModels(query))
    }
}