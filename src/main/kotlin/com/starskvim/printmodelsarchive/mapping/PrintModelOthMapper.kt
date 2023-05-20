package com.starskvim.printmodelsarchive.mapping

import com.starskvim.printmodelsarchive.domain.image.ImageService
import com.starskvim.printmodelsarchive.persistance.model.PrintModelOthData
import com.starskvim.printmodelsarchive.rest.model.PrintModelOth
import kotlinx.coroutines.runBlocking
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.beans.factory.annotation.Autowired

@Mapper
abstract class PrintModelOthMapper {

    @Autowired
    private lateinit var imageService: ImageService

    @Mapping(target = "preview", ignore = true)
    abstract fun dataToApi(source: PrintModelOthData): PrintModelOth

    @AfterMapping
    fun setPreview(source: PrintModelOthData, @MappingTarget target: PrintModelOth) {
        runBlocking {
            if (source.isImage()) {
                target.preview = imageService.getUrlForImage(source.storageName!!)
            }
        }
    }
}