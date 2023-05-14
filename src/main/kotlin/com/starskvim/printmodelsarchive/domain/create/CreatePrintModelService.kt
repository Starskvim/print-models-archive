package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import org.springframework.stereotype.Service

@Service
class CreatePrintModelService(

    private val scanService: FolderScanService,
    private val dataService: PrintModelDataService

) {

    @LoggTime
    suspend fun initializeArchive() {
        val files = scanService.getFilesFromDisk()
        InitializeArchiveTask(dataService, files).execute()
    }
}