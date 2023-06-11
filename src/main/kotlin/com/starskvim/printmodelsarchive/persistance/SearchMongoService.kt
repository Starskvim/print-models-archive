package com.starskvim.printmodelsarchive.persistance

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

interface SearchMongoService {

    fun addIsCriteria(query: Query, field: String, value: Any?): Query {
        if (value != null) {
            query.addCriteria(Criteria.where(field).`is`(value))
        }
        return query
    }

    fun addIsLikeCriteria(query: Query, field: String, value: String?): Query {
        if (value != null) {
            query.addCriteria(Criteria.where(field).regex(".*${value}.*", "i"))
        }
        return query
    }

    fun addInCriteria(query: Query, field: String, value: String?): Query {
        if (!value.isNullOrBlank()) {
            query.addCriteria(Criteria.where(field).`in`(value))
        }
        return query
    }

    fun addExcludeFieldsCriteria(query: Query, vararg fields: String): Query {
        for (field in fields) query.fields().exclude(field)
        return query
    }
}