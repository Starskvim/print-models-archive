package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelZipData
import com.starskvim.print.models.archive.rest.model.PrintModelZip
import org.mapstruct.Mapper

@Mapper
abstract class PrintModelZipMapper {

    abstract fun dataToApi(source: PrintModelZipData): PrintModelZip

}