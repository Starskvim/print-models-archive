package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.domain.CategoriesInfoService
import com.starskvim.printmodelsarchive.domain.MinioService
import com.starskvim.printmodelsarchive.domain.TaskProgressService
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
import com.starskvim.printmodelsarchive.utils.Executable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.apache.commons.collections4.ListUtils.partition
import org.apache.commons.io.FilenameUtils.getExtension
import java.io.File
import java.time.LocalDate.now
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class InitializeArchiveTask(

    private val dataService: PrintModelDataService,
    private val minioService: MinioService,
    private val categoriesInfoService: CategoriesInfoService,
    private val taskProgressService: TaskProgressService,
    private var files: MutableCollection<File>,

    ) : Executable {

    private var models = ConcurrentHashMap<String, PrintModelData>()
    private val modelNames = CopyOnWriteArraySet<String>()
    private var filesCount = 0
    private var fileDone = AtomicInteger(0)

    @LoggTime
    override suspend fun execute() {
        filesCount = files.size
        val dispatcher = Executors
            .newFixedThreadPool(6)
            .asCoroutineDispatcher()
        coroutineScope {
            files.map { file ->
                async(dispatcher) {
                    createModels(file)
                }
            }.awaitAll()
        }
        models.values.forEach { linkPreview(it) }
        files.clear()
        categoriesInfoService.initializeCategoriesInfo(models.values)
        val modelsList = mutableListOf<PrintModelData>()
        modelsList.addAll(models.values)
        val modelsPages = partition(modelsList, 100)
        modelsPages.forEach { dataService.saveAll(it) }
    }

    suspend fun createModels(file: File) {
        val modelName = file.parentFile.name
        if (modelNames.contains(modelName)) {
            createNonModelObject(file, modelName)
        } else {
            createModel(file, modelName)
            createNonModelObject(file, modelName)
            logger.info { "Model - create - $modelName" }
        }
        fileDone.incrementAndGet()
        logger.info { "$fileDone/$filesCount - now add - ${file.name}" }
        incrementProgress("$fileDone/$filesCount - now add - ${file.name}")
    }

    private fun createModel(file: File, folderName: String): PrintModelData {
        val modelName = clearModelName(folderName)
        val modelCategory = getPrintModelCategory(file)
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
            now(),
            null
        )
        modelNames.add(folderName)
        if (models.contains(folderName)) return printModel
        models[folderName] = printModel
        return printModel
    }

    private suspend fun createNonModelObject(file: File, modelName: String) {
        if (ZIP_FORMATS.contains(getExtension(file.name))) {
            createZip(file, modelName)
        } else {
            createOth(file, modelName)
        }
    }

    private fun createZip(file: File, modelName: String) {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val ratio = 0 // TODO implement
        val zip = PrintModelZipData(
            file.name,
            file.absolutePath,
            format,
            size,
            ratio
        )
        linkZipWithModel(modelName, zip)
    }

    private fun linkZipWithModel(folderName: String, zip: PrintModelZipData) {
        models[folderName]?.zips?.add(zip)
    }

    private suspend fun createOth(file: File, modelName: String): PrintModelOthData {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val oth = PrintModelOthData(
            file.name,
            file.absolutePath,
            format,
            size,
            addImageInS3(file, format, modelName)
        )
        linkOthWithModel(modelName, oth)
        return oth
    }

    suspend fun addImageInS3(file: File, format: String, modelName: String): String? {
        if (IMAGE_FORMATS_TRIGGERS.contains(format)) {
            val storageName = getStorageName(modelName, file.name)
            minioService.saveImageWithCompressing(file, storageName)
            return storageName
        }
        return null
    }

    fun linkOthWithModel(folderName: String, oth: PrintModelOthData) {
        models[folderName]?.oths?.add(oth)
    }

    private fun linkPreview(model: PrintModelData) {
        for (oth in model.oths!!) {
            if (oth.isImage()) {
                model.preview = oth.storageName
                break
            }
        }
    }

    private suspend fun incrementProgress(currentTask: String) {
        taskProgressService.incrementTask(INITIALIZE_ARCHIVE_TASK, currentTask, filesCount)
    }

    companion object : KLogging()
}
