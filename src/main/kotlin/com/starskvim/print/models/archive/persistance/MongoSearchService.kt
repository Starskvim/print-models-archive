package com.starskvim.print.models.archive.persistance

import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

interface MongoSearchService { // java style :(

    fun Query.addIsNotNullCriteria(field: String): Query {
        this.addCriteria(Criteria.where(field).ne(null))
        return this
    }

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

    fun addInCriteria(query: Query, field: String, value: List<String>?): Query {
        if (!value.isNullOrEmpty()) {
            query.addCriteria(Criteria.where(field).`in`(value))
        }
        return query
    }

    fun addExcludeFieldsCriteria(query: Query, vararg fields: String): Query {
        for (field in fields) query.fields().exclude(field)
        return query
    }

    fun addGteCriteria(query: Query, field: String, value: Int?): Query {
        if (value != null) {
            query.addCriteria(Criteria.where(field).gte(value))
        }
        return query
    }

    fun addSort(
        query: Query,
        direction: Direction,
        field: String
    ) = query.apply { with(by(direction, field)) }
}