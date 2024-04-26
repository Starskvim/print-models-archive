package com.starskvim.printmodelsarchive.config.mongo

import com.starskvim.printmodelsarchive.persistance.model.Auditable
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now

@Component
class Auditing : AbstractMongoEventListener<Any>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<Any>) {
        val source = event.source
        if (source is Auditable) {
            source.modifiedAt = now()
        }
        super.onBeforeConvert(event)
    }
}
