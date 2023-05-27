package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.persistance.CategoriesInfoDataService
import com.starskvim.printmodelsarchive.persistance.model.CategoriesInfoData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import org.springframework.stereotype.Service
import java.time.LocalDate.now
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service
class CategoriesInfoService(

    private val dataService: CategoriesInfoDataService

) {

    suspend fun initializeCategoriesInfo(models: CopyOnWriteArrayList<PrintModelData>): CategoriesInfoData {
        val modelsCategories = mutableListOf<String>()
        for (model in models) modelsCategories.addAll(model.categories!!)
        val uniqCategories = mutableSetOf<String>()
        uniqCategories.addAll(modelsCategories)
        val categoriesCount = mutableMapOf<String, Int>()
        for (category in uniqCategories) categoriesCount[category] = Collections.frequency(uniqCategories, category)
        val categories = mutableListOf<String>()
        categories.addAll(uniqCategories)
        val categoriesInfo = CategoriesInfoData(
            CATEGORIES_INFO,
            categories,
            categoriesCount,
            now(),
            null
        )
        return dataService.saveCategoriesInfo(categoriesInfo)
    }

    suspend fun getAllCategories(): List<String> = dataService.getAllCategories()
}
