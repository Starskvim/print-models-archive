package com.starskvim.print.models.archive.domain.create

import com.starskvim.print.models.archive.aop.LoggTime
import com.starskvim.print.models.archive.config.ArchiveConfiguration
import com.starskvim.print.models.archive.domain.CategoriesInfoService
import com.starskvim.print.models.archive.domain.image.MinioService
import com.starskvim.print.models.archive.domain.model.initialize.InitializeArchiveTaskContext
import com.starskvim.print.models.archive.domain.model.initialize.InitializePrintModelData
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelOthData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelZipData
import com.starskvim.print.models.archive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import com.starskvim.print.models.archive.utils.Constants.Triggers.IMAGE_FORMATS_TRIGGERS
import com.starskvim.print.models.archive.utils.Constants.Triggers.NSFW_TRIGGERS
import com.starskvim.print.models.archive.utils.Constants.Triggers.ZIP_FORMATS
import com.starskvim.print.models.archive.utils.CreateUtils.clearModelName
import com.starskvim.print.models.archive.utils.CreateUtils.getAllPrintModelCategories
import com.starskvim.print.models.archive.utils.CreateUtils.getMyRateForModel
import com.starskvim.print.models.archive.utils.CreateUtils.getPrintModelCategory
import com.starskvim.print.models.archive.utils.CreateUtils.getSizeFileDouble
import com.starskvim.print.models.archive.utils.CreateUtils.getStorageName
import com.starskvim.print.models.archive.utils.CreateUtils.isHaveTrigger
import com.starskvim.print.models.archive.utils.DateUtils.dateTimeFromLong
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.apache.commons.io.FilenameUtils.getExtension
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime.now

// todo separate logic
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

    suspend fun clearArchive() {
        categoriesInfoService.deleteAll()
        dataService.deleteAll()
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
        associateModelContextAndFiles(context) // TODO parallel stage ?
        context.prepareResultsModels()
        context.models.forEach { linkPreview(it) }
        context.apply {
            files.clear()
            contextByModelName.clear()
        }
        categoriesInfoService.initializeCategoriesInfo(context.models)
        context.models.chunked(config.saveBatch)
            .forEach {
                dataService.saveAll(it)
            }
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
        logger.info { "${context.fileDone}/${context.filesCount} - processed - ${file.name}" }
        incrementProgress(
            "${context.fileDone}/${context.filesCount} - processed - ${file.name}",
            context
        )
    }

    private suspend fun associateModelContextAndFiles(
        context: InitializeArchiveTaskContext
    ) {
        context.oths.forEach {
            context.contextByModelName[it.parentFileName]?.oths?.add(it)
        }
        context.zips.forEach {
            context.contextByModelName[it.parentFileName]?.zips?.add(it)
        }
    }

    private suspend fun createModel(
        file: File,
        folderName: String,
        context: InitializeArchiveTaskContext
    ): PrintModelData {
        val modelName = clearModelName(folderName)
        val modelCategory = getPrintModelCategory(file.path)
        val myRate = getMyRateForModel(folderName)
        val nsfwFlag = isHaveTrigger(file.absolutePath, NSFW_TRIGGERS)
        val createdAt = now()
        val printModel = PrintModelData(
            id = null,
            preview = null,
            modelName = modelName,
            folderName = folderName,
            path = file.parent,
            category = modelCategory,
            rate = myRate,
            nsfw = nsfwFlag,
            categories = getAllPrintModelCategories(file.path),
            zips = mutableListOf(),
            oths = mutableListOf(),
            addedAt = dateTimeFromLong(file.lastModified()),
            createdAt = createdAt,
            modifiedAt = createdAt
        )
        context.modelNames.add(folderName)
        if (context.contextByModelName.contains(folderName)) return printModel // ?
        context.contextByModelName[folderName] = InitializePrintModelData(printModel)
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
        context.apply { oths.add(oth) }
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
        if (model.oths == null) return
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