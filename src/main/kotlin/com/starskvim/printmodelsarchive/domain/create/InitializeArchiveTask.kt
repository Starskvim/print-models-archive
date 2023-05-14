package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.domain.Executable
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelOthData
import com.starskvim.printmodelsarchive.persistance.model.PrintModelZipData
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.NSFW_TRIGGERS
import com.starskvim.printmodelsarchive.utils.Constants.Triggers.ZIP_FORMATS
import com.starskvim.printmodelsarchive.utils.CreateUtils.detectMyRateForModel
import com.starskvim.printmodelsarchive.utils.CreateUtils.detectPrintModelCategory
import com.starskvim.printmodelsarchive.utils.CreateUtils.detectTrigger
import com.starskvim.printmodelsarchive.utils.CreateUtils.getSizeFileDouble
import mu.KLogging
import org.apache.commons.collections4.ListUtils.partition
import org.apache.commons.io.FilenameUtils.getExtension
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

class InitializeArchiveTask(

    private val dataService: PrintModelDataService,
    private var files: Collection<File>,

    private var models: CopyOnWriteArrayList<PrintModelData> = CopyOnWriteArrayList<PrintModelData>(), // before its set

    private val modelNames: CopyOnWriteArraySet<String> = CopyOnWriteArraySet<String>(),
    private var filesCount: Int = 0,
    private var fileDone: AtomicInteger = AtomicInteger(0)

) : Executable {
    override suspend fun execute() {
        filesCount = files.size
        files.parallelStream().forEach { createModels(it) }
        files = emptyList()
        val modelsPages = partition(models, 100)
        models.clear()
        for (page in modelsPages) {
            dataService.saveAll(models)
        }
    }

    private fun createModels(file: File) {
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

    private fun createModel(file: File, modelName: String) {
        val modelCategory = detectPrintModelCategory(file)
        val myRate = detectMyRateForModel(modelName)
        val nsfwFlag = detectTrigger(file.absolutePath, NSFW_TRIGGERS)
        val printModel = PrintModelData(
            modelName,
            file.parent,
            modelCategory,
            myRate,
            nsfwFlag,
            mutableListOf(),
            mutableListOf(),
            null,
            null,
            null
        )
        modelNames.add(modelName)
        models.add(printModel)
    }

    private fun createNonModelObject(file: File, modelName: String) {
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

    private fun createOth(file: File, modelName: String) {
        val size = getSizeFileDouble(file)
        val format = getExtension(file.name)
        val oth = PrintModelOthData(
            file.name,
            file.absolutePath,
            format,
            size,
            null // TODO implement
        )
        linkOthWithModel(modelName, oth)
    }

    private fun linkOthWithModel(modelName: String, oth: PrintModelOthData) {
        for (model in models) {
            if (model.modelName == modelName) {
                model.oths?.add(oth)
                break
            }
        }
    }

    companion object : KLogging()
}