package com.starskvim.print.models.archive.domain.create

import com.starskvim.print.models.archive.config.ArchiveConfiguration
import com.starskvim.print.models.archive.domain.image.MinioService
import com.starskvim.print.models.archive.domain.model.initialize.ArchiveTaskContext
import com.starskvim.print.models.archive.domain.model.initialize.InitializePrintModelData
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelOthData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelZipData
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
import com.starskvim.print.models.archive.utils.CreateUtils.linkPreview
import com.starskvim.print.models.archive.utils.DateUtils.dateTimeFromLong
import mu.KLogger
import org.apache.commons.io.FilenameUtils.getExtension
import java.io.File
import java.time.LocalDateTime.now

abstract class AbstractArchiveProcessor(
    open val dataService: PrintModelDataService,
    open val minioService: MinioService,
    open val taskProgressService: TaskProgressService,
    open val config: ArchiveConfiguration
) {

    abstract suspend fun typeTask(): String

    abstract suspend fun process(
        context: ArchiveTaskContext
    ): ArchiveTaskContext

    protected suspend fun postProcess(
        context: ArchiveTaskContext
    ): ArchiveTaskContext {
        return context.apply {
            associateModelContextAndFiles()
            prepareResultsModels()
            models.forEach { linkPreview(it) }
            files.clear()
            contextByModelName.clear()
            models.chunked(config.saveBatch)
                .forEach { dataService.saveAll(it) }
        }
    }

    protected suspend fun createModelsAndFiles(
        file: File,
        context: ArchiveTaskContext,
        logger: KLogger
    ) {
        val folderName = file.parentFile.name
        if (context.modelNames.contains(folderName)) {
            createNonEntityObject(file, folderName, context)
        } else {
            createModelEntity(file, folderName, context)
            createNonEntityObject(file, folderName, context)
            logger.info { "Model - create - $folderName" }
        }
        context.fileDone.incrementAndGet()
        logger.info { "File ${context.fileDone}/${context.filesCount} - processed - ${file.name}" }
        incrementProgress(
            "File ${context.fileDone}/${context.filesCount} - processed - ${file.name}",
            context
        )
    }

    // TODO lock for add in concurrent ?
    private fun createModelEntity(
        file: File,
        folderName: String,
        context: ArchiveTaskContext
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
            meta = null, // TODO meta from file
            createdAt = createdAt,
            modifiedAt = createdAt
        )
        context.modelNames.add(folderName)
        if (context.contextByModelName.contains(folderName)) return printModel // ?
        context.contextByModelName[folderName] = InitializePrintModelData(printModel)
        return printModel
    }

    private suspend fun createNonEntityObject(
        file: File,
        parentFileName: String,
        context: ArchiveTaskContext
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
        context: ArchiveTaskContext
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
        context: ArchiveTaskContext
    ): PrintModelOthData {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val oth = PrintModelOthData(
            parentFileName = parentFileName,
            fileName = file.name,
            path = file.absolutePath,
            format = format,
            size = size,
            storageName = addImageInS3(file, format, parentFileName) // TODO after all save
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

    private suspend fun incrementProgress(
        currentTask: String,
        context: ArchiveTaskContext
    ) {
        taskProgressService.incrementTask(
            typeTask(),
            currentTask,
            context.filesCount
        )
    }
}