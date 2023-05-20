package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.persistance.CategoriesInfoDataService
import org.springframework.stereotype.Service

@Service
class CategoriesInfoService(

    private val dataService: CategoriesInfoDataService

) {

    suspend fun getAllCategories(): List<String> = dataService.getAllCategories()
}