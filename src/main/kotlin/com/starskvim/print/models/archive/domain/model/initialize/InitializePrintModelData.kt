package com.starskvim.print.models.archive.domain.model.initialize

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelOthData
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelZipData
import java.util.concurrent.CopyOnWriteArraySet

class InitializePrintModelData(

    val model: PrintModelData

) {
    // bad deduplication
    var oths = CopyOnWriteArraySet<PrintModelOthData>()
    var zips = CopyOnWriteArraySet<PrintModelZipData>()
}