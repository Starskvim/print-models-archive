package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.persistance.CategoriesInfoDataService
import com.starskvim.printmodelsarchive.persistance.model.CategoriesInfoData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.rest.model.Category
import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.springframework.stereotype.Service
import java.time.LocalDate.now
import java.util.*


@Service
class CategoriesInfoService(
    private val dataService: CategoriesInfoDataService
) {

    // todo add main category in child
    suspend fun initializeCategoriesInfo(models: MutableCollection<PrintModelData>): CategoriesInfoData {
        val modelsCategories = mutableListOf<String>()
        for (model in models) modelsCategories.addAll(model.categories!!)
        val uniqCategories = mutableSetOf<String>()
        uniqCategories.addAll(modelsCategories)
        val categoriesCount = mutableMapOf<String, Int>()
        for (category in uniqCategories) categoriesCount[category] = Collections.frequency(uniqCategories, category) // todo not work
        val categories = mutableListOf<String>()
        categories.addAll(uniqCategories)
        val categoriesInfo = CategoriesInfoData(
            CATEGORIES_INFO,
            categories,
            categoriesCount,
            null,
            now(),
            null
        )
        return dataService.saveCategoriesInfo(categoriesInfo)
    }

    // todo test
    private suspend fun createCatalog(models: MutableCollection<PrintModelData>) {
        val directedGraph = DefaultDirectedGraph<Any, DefaultEdge>(
            DefaultEdge::class.java
        )
        directedGraph.addVertex("v1")
        directedGraph.addVertex("v2")
        directedGraph.addVertex("v3")
        directedGraph.addEdge("v1", "v2")
    }

    suspend fun deleteAll() = dataService.deleteAll()

    suspend fun getAllCategories(): List<Category> = dataService.getAllCategories()
}
