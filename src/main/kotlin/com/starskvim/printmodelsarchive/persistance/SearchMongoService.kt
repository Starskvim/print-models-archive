package com.starskvim.printmodelsarchive.persistance

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

interface SearchMongoService {

    fun addIsCriteria(query: Query, field : String, value : Any?) : Query {
        if (value != null) {
            query.addCriteria(Criteria.where(field).`is`(value))
        }
        return query
    }
}