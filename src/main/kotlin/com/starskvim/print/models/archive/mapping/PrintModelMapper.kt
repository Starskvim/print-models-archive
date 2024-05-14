package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.domain.image.ImageService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModel
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelCard
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelSuggest
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

    @Mapping(target = "preview", ignore = true)
    @Mapping(target = "mainCategory", ignore = true)
    abstract fun dataToSuggestApi(source: PrintModelData): PrintModelSuggest

    abstract fun dataToSuggestApi(source: List<PrintModelData>): List<PrintModelSuggest>

    @AfterMapping
    fun setPreview(source: PrintModelData, @MappingTarget target: PrintModel) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getBucketForImage(source.preview!!)
            }
        }
    }

    @AfterMapping
    fun setPreview(source: PrintModelData, @MappingTarget target: PrintModelCard) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getBucketForImage(source.preview!!)
            }
            if (source.hasImages()) {
                target.images = source.oths!!
                    .filter { it.isImage() }
                    .map { imageService.getBucketForImage(it.fileName!!) }
                    .toList()
            }
        }
    }

    @AfterMapping
    fun setPreviewAndMainCategory(source: PrintModelData, @MappingTarget target: PrintModelSuggest) {
        runBlocking {
            if (source.hasPreview()) {
                target.preview = imageService.getBucketForImage(source.preview!!)
                target.mainCategory = source.categories?.last()
            }
        }
    }
}