package com.starskvim.printmodelsarchive.mapping

import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import org.mapstruct.Mapper

@Mapper(uses = [PrintModelOthMapper::class, PrintModelZipMapper::class])
abstract class PrintModelMapper {

    abstract fun dataToApi(source: PrintModelData): PrintModel

    abstract fun dataToApi(source: List<PrintModelData>): List<PrintModel>

}