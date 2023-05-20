package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.domain.CategoriesInfoService
import com.starskvim.printmodelsarchive.domain.MinioService
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import org.springframework.stereotype.Service

@Service
class CreatePrintModelService(

    private val scanService: FolderScanService,
    private val dataService: PrintModelDataService,
    private val minioService: MinioService,
    private val categoriesInfoService: CategoriesInfoService

    ) {

    @LoggTime
    suspend fun initializeArchive() {
        val files = scanService.getFilesFromDisk()
        InitializeArchiveTask(dataService, minioService, categoriesInfoService, files).execute()
    }
}