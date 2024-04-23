package com.starskvim.printmodelsarchive.mapping

import com.starskvim.printmodelsarchive.domain.image.ImageService
import com.starskvim.printmodelsarchive.persistance.model.print_model.PrintModelData
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import kotlinx.coroutines.runBlocking
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.beans.factory.annotation.Autowired

@Mapper(uses = [PrintModelOthMapper::class, PrintModelZipMapper::class])
abstract class PrintModelMapper {

    @Autowired
    private lateinit var imageService: ImageService

    @Mapping(target = "preview", ignore = true)
    abstract fun dataToApi(source: PrintModelData?): PrintModel?

    abstract fun dataToApi(source: List<PrintModelData?>): List<PrintModel?>

    @AfterMapping
    fun setPreview(source: PrintModelData, @MappingTarget target: PrintModel) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getUrlForImage(source.preview!!)
            }
        }
    }
}