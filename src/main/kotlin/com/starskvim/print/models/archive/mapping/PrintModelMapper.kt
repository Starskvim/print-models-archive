package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.domain.image.ImageService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModel
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelCard
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

    @Mapping(target = "preview", ignore = true)
    @Mapping(target = "images", ignore = true)
    abstract fun dataToCardApi(source: PrintModelData?): PrintModelCard

    abstract fun dataToApi(source: List<PrintModelData?>): List<PrintModel?>

    abstract fun dataToCardApi(source: List<PrintModelData?>): List<PrintModelCard?>

    @AfterMapping
    fun setPreview(source: PrintModelData, @MappingTarget target: PrintModel) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getUrlForImage(source.preview!!)
            }
        }
    }

    @AfterMapping
    fun setPreview(source: PrintModelData, @MappingTarget target: PrintModelCard) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getUrlForImage(source.preview!!)
            }
            if (source.hasImages()) {
                target.images = source.oths!!
                    .filter { it.isImage() }
                    .map { imageService.getUrlForImage(it.fileName!!) }
                    .toList()
            }
        }
    }
}