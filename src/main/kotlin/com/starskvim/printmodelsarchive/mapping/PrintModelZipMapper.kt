package com.starskvim.printmodelsarchive.mapping

import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelZipData
import com.starskvim.printmodelsarchive.rest.model.PrintModelZip
import org.mapstruct.Mapper

@Mapper
abstract class PrintModelZipMapper {

    abstract fun dataToApi(source: PrintModelZipData): PrintModelZip

}