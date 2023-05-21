package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.domain.CategoriesInfoService
import com.starskvim.printmodelsarchive.domain.MinioService
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelOthData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelZipData
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.IMAGE_FORMATS_TRIGGERS
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.NSFW_TRIGGERS
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.ZIP_FORMATS
import com.starskvim.printmodelsarchive.utils.CreateUtils.getAllPrintModelCategories
import com.starskvim.printmodelsarchive.utils.CreateUtils.getMyRateForModel
import com.starskvim.printmodelsarchive.utils.CreateUtils.getPrintModelCategory
import com.starskvim.printmodelsarchive.utils.CreateUtils.getSizeFileDouble
import com.starskvim.printmodelsarchive.utils.CreateUtils.getStorageName
import com.starskvim.printmodelsarchive.utils.CreateUtils.isHaveTrigger
import com.starskvim.printmodelsarchive.utils.Executable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.apache.commons.collections4.ListUtils.partition
import org.apache.commons.io.FilenameUtils.getExtension
import java.io.File
import java.time.LocalDate.now
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

class InitializeArchiveTask(

    private val dataService: PrintModelDataService,
    private val minioService: MinioService,
    private val categoriesInfoService: CategoriesInfoService,
    private var files: Collection<File>,

) : Executable {

    // TODO  concurrent create bug
    private var models: CopyOnWriteArrayList<PrintModelData> = CopyOnWriteArrayList<PrintModelData>() // before its set
    private val modelNames: CopyOnWriteArraySet<String> = CopyOnWriteArraySet<String>()
    private var filesCount: Int = 0
    private var fileDone: AtomicInteger = AtomicInteger(0)

    override suspend fun execute() {
        filesCount = files.size
        coroutineScope {
            files.map { file ->
                async {
                    createModels(file)
                }
            }.awaitAll()
        }
        models.forEach { linkPreview(it) }
        files = emptyList()
        categoriesInfoService.initializeCategoriesInfo(models)
        val modelsPages = partition(models, 100)
        for (page in modelsPages) {
            dataService.saveAll(page)
        }
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

    }

    private fun createModel(file: File, modelName: String): PrintModelData {
        val modelCategory = getPrintModelCategory(file)
        val myRate = getMyRateForModel(modelName)
        val nsfwFlag = isHaveTrigger(file.absolutePath, NSFW_TRIGGERS)
        val printModel = PrintModelData(
            null,
            null,
            modelName,
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
        modelNames.add(modelName)
        models.add(printModel)
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

    private fun linkZipWithModel(modelName: String, zip: PrintModelZipData) {
        for (model in models) {
            if (model.modelName == modelName) {
                model.zips?.add(zip)
                break
            }
        }
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

    fun linkOthWithModel(modelName: String, oth: PrintModelOthData) {
        for (model in models) {
            if (model.modelName == modelName) {
                model.oths!!.add(oth)
                break
            }
        }
    }

    private fun linkPreview(model: PrintModelData) {
        for (oth in model.oths!!) {
            if (oth.isImage()) {
                model.preview = oth.storageName
                break
            }
        }
    }

    companion object : KLogging()
}
