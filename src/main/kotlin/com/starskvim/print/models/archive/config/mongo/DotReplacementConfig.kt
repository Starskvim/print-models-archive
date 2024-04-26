package com.starskvim.print.models.archive.config.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MappingMongoConverter

@Configuration
class DotReplacementConfig {

    @Autowired
    fun setMapKeyDotReplacement(mappingMongoConverter: MappingMongoConverter) {
        mappingMongoConverter.setMapKeyDotReplacement("DOT")
    }
}