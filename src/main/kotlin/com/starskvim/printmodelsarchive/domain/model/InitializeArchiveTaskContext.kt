package com.starskvim.printmodelsarchive.domain.model

import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelData
import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelOthData
import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelZipData
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

class InitializeArchiveTaskContext(

    var files: MutableCollection<File>

) {

    var models = ConcurrentHashMap<String, PrintModelData>()
    var otns = CopyOnWriteArrayList<PrintModelOthData>()
    var zips = CopyOnWriteArrayList<PrintModelZipData>()
    val modelNames = CopyOnWriteArraySet<String>()
    var filesCount = 0
    var fileDone = AtomicInteger(0)

}