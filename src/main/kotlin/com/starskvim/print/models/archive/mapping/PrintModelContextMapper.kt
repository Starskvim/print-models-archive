package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.domain.model.PrintModelContext
import com.starskvim.print.models.archive.persistance.model.print_model.meta.ImageMeta
import com.starskvim.print.models.archive.persistance.model.print_model.meta.Meta
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

/**
 * Patch-маппер локального контекста: null-поля источника пропускаются,
 * а вложенные коллекции [Meta] дополняются, а не замещаются.
 */
@Mapper
abstract class PrintModelContextMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun update(source: PrintModelContext, @MappingTarget target: PrintModelContext)

    /**
     * Используется MapStruct для вложенного поля `meta`:
     * processors — объединение множеств, images — слияние по ключу fileName + processor.
     */
    fun patchMeta(source: Meta, @MappingTarget target: Meta) {
        target.processors.addAll(source.processors)
        source.images.forEach { image ->
            val existing = target.images.firstOrNull { it.sameKey(image) }
            if (existing == null) {
                target.images.add(image)
            } else {
                patchImageMeta(image, existing)
            }
        }
    }

    /**
     * Запись с тем же fileName + processor: tags — объединение без дублей.
     */
    fun patchImageMeta(source: ImageMeta, @MappingTarget target: ImageMeta) {
        target.tags = (target.tags + source.tags).distinct()
    }

    private fun ImageMeta.sameKey(other: ImageMeta): Boolean =
        fileName == other.fileName && processor == other.processor
}
