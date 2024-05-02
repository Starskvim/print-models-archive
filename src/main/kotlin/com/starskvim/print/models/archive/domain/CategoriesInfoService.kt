package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.persistance.CategoriesInfoDataService
import com.starskvim.print.models.archive.persistance.model.catalog.Catalog
import com.starskvim.print.models.archive.persistance.model.catalog.CategoriesInfoData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.Category
import com.starskvim.print.models.archive.utils.Constants.Document.CATEGORIES_INFO
import com.starskvim.print.models.archive.utils.Constants.Service.ONE_INT
import com.starskvim.print.models.archive.utils.Constants.Service.ZERO_INT
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import com.starskvim.print.models.archive.persistance.model.catalog.Category as CategoryData
import com.starskvim.print.models.archive.rest.model.Catalog as CatalogApi


@Service
class CategoriesInfoService(
    private val dataService: CategoriesInfoDataService
) {

    // TODO cache
    suspend fun getCatalog(): CatalogApi {
        val catalogData = dataService.getCategoriesInfoData().categoriesCatalog
        return CatalogApi(
            catalog = catalogData.catalog.filter { it.level == ZERO_INT }
        )
    }

    suspend fun initializeCategoriesInfo(
        models: Collection<PrintModelData>
    ): CategoriesInfoData {
        val modelsCategories = mutableListOf<String>()
        for (model in models) modelsCategories.addAll(model.categories!!)
        val uniqCategories = mutableSetOf<String>()
        uniqCategories.addAll(modelsCategories)
        val categories = mutableListOf<String>()
        categories.addAll(uniqCategories)
        val categoriesInfo = CategoriesInfoData(
            CATEGORIES_INFO,
            categories,
            createCatalog(models),
            now(),
            now()
        )
        return dataService.saveCategoriesInfo(categoriesInfo)
    }

    suspend fun deleteAll() = dataService.deleteAll()

    suspend fun getAllCategories(): List<Category> = dataService.getAllCategories()

    private suspend fun createCatalog(
        models: Collection<PrintModelData>
    ): Catalog {
        val categories = mutableMapOf<String, CategoryData>()
        models.forEach {
            it.categories?.addBranch(categories)
        }
        return Catalog(categories.values)
    }

    private suspend fun MutableList<String>.addBranch(
        categories: MutableMap<String, CategoryData>
    ) {
        if (isEmpty()) return
        var lastCategory: CategoryData? = null
        forEach { categoryName ->
            val existingCategory = categories[categoryName]
            if (existingCategory == null) {
                val newCategory = createNewCategory(categoryName, lastCategory)
                categories[categoryName] = newCategory
                lastCategory?.children?.add(newCategory)
                lastCategory = newCategory
            } else {
                updateExistingCategory(existingCategory)
                lastCategory = existingCategory
            }
        }
    }

    private suspend fun createNewCategory(
        categoryName: String,
        lastCategory: CategoryData?
    ): CategoryData {
        return CategoryData(
            name = categoryName,
            children = mutableListOf(),
            size = ONE_INT,
            level = lastCategory?.level?.plus(ONE_INT) ?: ZERO_INT
        )
    }

    private fun updateExistingCategory(category: CategoryData) {
        category.size += 1
    }
}
//
//fun main(args: Array<String>) {
//    val categories = mutableMapOf<String, CategoryData>()
//    val input = mutableListOf<MutableList<String>>()
//
//    input.add(mutableListOf("pack", "theme1", "author1"))
//    input.add(mutableListOf("pack", "theme1", "author2"))
//    input.add(mutableListOf("figure", "theme3", "author3"))
//    input.add(mutableListOf("pack", "theme2", "author4"))
//    input.add(mutableListOf("figure", "theme3", "author5"))
//
//    input.forEach { it.addBranch3(categories) }
//
//    val result = categories.values
//    println()
//}
//
//object Test {
//
//    fun MutableList<String>.addBranch3(categories: MutableMap<String, CategoryData>) {
//        if (isEmpty()) return
//        var lastCategory: CategoryData? = null
//        forEach { categoryName ->
//            val existingCategory = categories[categoryName]
//            if (existingCategory == null) {
//                val newCategory = createNewCategory(categoryName, lastCategory)
//                categories[categoryName] = newCategory
//                lastCategory?.children?.add(newCategory)
//                lastCategory = newCategory
//            } else {
//                updateExistingCategory(existingCategory)
//                lastCategory = existingCategory
//            }
//        }
//    }
//    private fun createNewCategory(
//        categoryName: String,
//        lastCategory: CategoryData?
//    ): CategoryData {
//        return CategoryData(
//            name = categoryName,
//            children = mutableListOf(),
//            size = 1,
//            level = lastCategory?.level?.plus(1) ?: 0
//        )
//    }
//    private fun updateExistingCategory(category: CategoryData) {
//        category.size += 1
//    }
//}