package com.starskvim.print.models.archive.rest.controller

import com.starskvim.print.models.archive.domain.CategoriesInfoService
import com.starskvim.print.models.archive.domain.print_model.PrintModelService
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.utils.PageUtils.getPagesCount
import com.starskvim.print.models.archive.utils.PageUtils.getRequestModel
import com.starskvim.print.models.archive.utils.PageUtils.getRequestPageable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/models")
class PrintModelController(
    private val service: PrintModelService,
    private val categoriesInfoService: CategoriesInfoService
) {

    @GetMapping
    suspend fun getModels(
        requestModel: Model?,
        requestPageable: Pageable?,
        @RequestParam("wordName", required = false) wordName: String?,
        @RequestParam("wordCategory", required = false) category: String?,
        @RequestParam("rate", required = false) rate: Int?
    ): String {
        val pageable = getRequestPageable(requestPageable)
        val model = getRequestModel(requestModel)
        val modelsPage = service.getPrintModelsPage(
            PrintModelSearchParams(wordName, category, rate),
            pageable
        )
        val categories = categoriesInfoService.getAllCategories()
        model.addAttribute("categories", categories)
        model.addAttribute("models", modelsPage)
        model.addAttribute("allPage", modelsPage.totalPages)
        model.addAttribute("wordName", wordName)
        model.addAttribute("wordCategory", category)
        model.addAttribute("rate", rate)
        model.addAttribute("currentPage", pageable.pageNumber)
        model.addAttribute("pageNumbers", getPagesCount(pageable.pageNumber, modelsPage.totalPages))
        return "models"
    }

    @GetMapping("/model")
    suspend fun getModel(
        model: Model,
        @RequestParam("modelId", required = false) modelId: String
    ): String {
        val result = service.getPrintModelById(modelId)
        model.addAttribute("printModel", result)
        return "model"
    }
}
