package com.starskvim.printmodelsarchive.rest.controller

import com.starskvim.printmodelsarchive.domain.CategoriesInfoService
import com.starskvim.printmodelsarchive.domain.PrintModelService
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import com.starskvim.printmodelsarchive.utils.PageUtils.getPagesCount
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
        model: Model,
        pageable: Pageable,
        @RequestParam("wordName", required = false) wordName: String,
        @RequestParam("wordCategory", required = false) category: String,
    ): String {
        val modelsPage = service.getPrintModelsPage(PrintModelSearchParams(wordName, category), pageable)
        val categories = categoriesInfoService.getAllCategories()
        model.addAttribute("modelTagList", categories)
        model.addAttribute("models", modelsPage)
        model.addAttribute("allPage", modelsPage.totalPages)
        model.addAttribute("wordName", wordName)
        model.addAttribute("wordCategory", category)
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
