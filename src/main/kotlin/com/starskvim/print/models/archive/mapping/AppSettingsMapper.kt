package com.starskvim.print.models.archive.mapping

import com.starskvim.print.models.archive.persistance.model.setting.AppSettingsData
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy

@Mapper
abstract class AppSettingsMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun update(source: AppSettingsData, @MappingTarget target: AppSettingsData)

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun update(source: AppSettingsData.AppProxy, @MappingTarget target: AppSettingsData.AppProxy)

}