package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.config.ArchiveConfiguration
import com.starskvim.printmodelsarchive.domain.CategoriesInfoService
import com.starskvim.printmodelsarchive.domain.MinioService
import com.starskvim.printmodelsarchive.domain.TaskProgressService
import com.starskvim.printmodelsarchive.domain.model.InitializeArchiveTaskContext
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelOthData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelZipData
import com.starskvim.printmodelsarchive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.IMAGE_FORMATS_TRIGGERS
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.NSFW_TRIGGERS
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.ZIP_FORMATS
import com.starskvim.printmodelsarchive.utils.CreateUtils.clearModelName
import com.starskvim.printmodelsarchive.utils.CreateUtils.getAllPrintModelCategories
import com.starskvim.printmodelsarchive.utils.CreateUtils.getMyRateForModel
import com.starskvim.printmodelsarchive.utils.CreateUtils.getPrintModelCategory
import com.starskvim.printmodelsarchive.utils.CreateUtils.getSizeFileDouble
import com.starskvim.printmodelsarchive.utils.CreateUtils.getStorageName
import com.starskvim.printmodelsarchive.utils.CreateUtils.isHaveTrigger
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.apache.commons.io.FilenameUtils.getExtension
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate

@Service
class CreatePrintModelService(
    private val scanService: FolderScanService,
    private val dataService: PrintModelDataService,
    private val minioService: MinioService,
    private val categoriesInfoService: CategoriesInfoService,
    private val taskProgressService: TaskProgressService,
    private val dispatcher: ExecutorCoroutineDispatcher,
    private val config: ArchiveConfiguration
) {

    suspend fun checkFolders() {
        val files = scanService.getFilesFromDisk()
        logger.info { "Files found: ${files.size}" }
    }

    @LoggTime
    suspend fun initializeArchive() {
        val files = scanService.getFilesFromDisk()
        val context = InitializeArchiveTaskContext(files)
        process(context)
    }

    suspend fun process(
        context: InitializeArchiveTaskContext
    ) {
        context.apply { filesCount = files.size }
        val fileParts = context.files.chunked(config.coroutineBatch)
        coroutineScope {
            fileParts.forEach { part ->
                part.map {
                    async(dispatcher) { createModelsAndFiles(it, context) }
                }.awaitAll()
            }
        }
        linkModelsAndFiles(context)
        context.models.values.forEach { linkPreview(it) }
        context.apply { files.clear() }
        categoriesInfoService.initializeCategoriesInfo(context.models.values)
        val modelsPages = context.models.values.chunked(config.saveBatch)
        modelsPages.forEach { dataService.saveAll(it) }
    }

    private suspend fun createModelsAndFiles(
        file: File,
        context: InitializeArchiveTaskContext
    ) {
        val parentFileName = file.parentFile.name
        if (context.modelNames.contains(parentFileName)) {
            createNonModelObject(file, parentFileName, context)
        } else {
            createModel(file, parentFileName, context)
            createNonModelObject(file, parentFileName, context)
            logger.info { "Model - create - $parentFileName" }
        }
        context.fileDone.incrementAndGet()
        logger.info { "${context.fileDone}/${context.filesCount} - now add - ${file.name}" }
        incrementProgress(
            "${context.fileDone}/${context.filesCount} - now add - ${file.name}",
            context
        )
    }

    private suspend fun linkModelsAndFiles(
        context: InitializeArchiveTaskContext
    ) {
        context.otns.forEach {
            context.models[it.parentFileName]?.oths?.add(it)
        }
        context.zips.forEach {
            context.models[it.parentFileName]?.zips?.add(it)
        }
    }

    private fun createModel(
        file: File,
        folderName: String,
        context: InitializeArchiveTaskContext
    ): PrintModelData {
        val modelName = clearModelName(folderName)
        val modelCategory = getPrintModelCategory(file.path)
        val myRate = getMyRateForModel(folderName)
        val nsfwFlag = isHaveTrigger(file.absolutePath, NSFW_TRIGGERS)
        val printModel = PrintModelData(
            null,
            null,
            modelName,
            folderName,
            file.parent,
            modelCategory,
            myRate,
            nsfwFlag,
            getAllPrintModelCategories(file.path),
            mutableListOf(),
            mutableListOf(),
            LocalDate.now(),
            null
        )
        context.modelNames.add(folderName)
        if (context.models.contains(folderName)) return printModel // ?
        context.models[folderName] = printModel
        return printModel
    }

    private suspend fun createNonModelObject(
        file: File,
        parentFileName: String,
        context: InitializeArchiveTaskContext
    ) {
        if (ZIP_FORMATS.contains(getExtension(file.name))) {
            createZip(file, parentFileName, context)
        } else {
            createOth(file, parentFileName, context)
        }
    }

    private fun createZip(
        file: File,
        parentFileName: String,
        context: InitializeArchiveTaskContext
    ) {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val ratio = 0 // TODO implement
        val zip = PrintModelZipData(
            parentFileName = parentFileName,
            fileName = file.name,
            path = file.absolutePath,
            format = format,
            size = size,
            ratio = ratio
        )
        context.apply { zips.add(zip) }
    }

    private suspend fun createOth(
        file: File,
        parentFileName: String,
        context: InitializeArchiveTaskContext
    ): PrintModelOthData {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val oth = PrintModelOthData(
            parentFileName = parentFileName,
            fileName = file.name,
            path = file.absolutePath,
            format = format,
            size = size,
            storageName = addImageInS3(file, format, parentFileName)
        )
        context.apply { otns.add(oth) }
        return oth
    }

    private suspend fun addImageInS3(
        file: File,
        format: String,
        parentFileName: String
    ): String? {
        if (IMAGE_FORMATS_TRIGGERS.contains(format)) {
            val storageName = getStorageName(parentFileName, file.name)
            minioService.saveImageWithCompressing(file, storageName)
            return storageName
        }
        return null
    }

    private fun linkPreview(model: PrintModelData) {
        for (oth in model.oths!!) {
            if (oth.isImage()) {
                model.preview = oth.storageName
                break
            }
        }
    }

    private suspend fun incrementProgress(
        currentTask: String,
        context: InitializeArchiveTaskContext
    ) {
        taskProgressService.incrementTask(
            INITIALIZE_ARCHIVE_TASK,
            currentTask,
            context.filesCount
        )
    }

    companion object : KLogging()
}