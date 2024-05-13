package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.domain.create.CreateArchiveProcessor
import com.starskvim.print.models.archive.domain.create.FolderScanService
import com.starskvim.print.models.archive.domain.create.UpdateArchiveProcessor
import com.starskvim.print.models.archive.domain.image.MinioService
import com.starskvim.print.models.archive.domain.model.initialize.ArchiveTaskContext
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class PrintModelAdminService(
    private val scanService: FolderScanService,
    private val dataService: PrintModelDataService,
    private val categoriesInfoService: CategoriesInfoService,
    private val createService: CreateArchiveProcessor,
    private val updateService: UpdateArchiveProcessor,
    private val minioService: MinioService
) {

    suspend fun initializeArchive() {
        val files = scanService.getFilesFromDisk()
        val postContext = createService.process(
            ArchiveTaskContext(files)
        )
        categoriesInfoService.initializeCategoriesInfo(postContext.models)
    }

    suspend fun updateArchive() {
        val files = scanService.getFilesFromDisk()
        val postContext = updateService.process(
            ArchiveTaskContext(files)
        )
        if (postContext.models.isEmpty()) return
        categoriesInfoService.updateCategoriesInfo(postContext.models)
    }

    suspend fun clearArchive() {
        categoriesInfoService.deleteAll()
        dataService.deleteAll()
    }

    suspend fun checkFolders() {
        val files = scanService.getFilesFromDisk()
        logger.info { "Files found: ${files.size}" }
    }

    suspend fun recreateBucket() {
        minioService.recreateBucket()
    }

    companion object : KLogging()
}