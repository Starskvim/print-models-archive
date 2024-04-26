package com.starskvim.print.models.archive.domain.model.initialize

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelOthData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelZipData
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

data class InitializeArchiveTaskContext(

    val files: MutableCollection<File>

) {

    val contextByModelName = ConcurrentHashMap<String, InitializePrintModelData>()
    val oths = CopyOnWriteArrayList<PrintModelOthData>()
    val zips = CopyOnWriteArrayList<PrintModelZipData>()
    val modelNames = CopyOnWriteArraySet<String>()
    var filesCount = 0
    var fileDone = AtomicInteger(0)

    lateinit var models: List<PrintModelData>

    fun prepareResultsModels() {
        models = contextByModelName.values
            .asSequence()
            .apply {
                forEach {
                    it.model.oths.addAll(it.oths)
                    it.model.zips.addAll(it.zips)
                }
            }.map { it.model }
            .toList()
    }
}