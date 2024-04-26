package com.starskvim.printmodelsarchive.domain.model.initialize

import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelData
import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelOthData
import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelZipData
import java.util.concurrent.CopyOnWriteArrayList

class InitializePrintModelData(

    val model: PrintModelData

) {
    var oths = CopyOnWriteArrayList<PrintModelOthData>()
    var zips = CopyOnWriteArrayList<PrintModelZipData>()
}