package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.domain.model.PrintModelContext
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

@Mapper
abstract class PrintModelContextMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun update(source: PrintModelContext, @MappingTarget target: PrintModelContext)

}