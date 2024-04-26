package com.starskvim.print.models.archive.utils

import org.springframework.data.domain.Pageable
import org.springframework.ui.ConcurrentModel
import org.springframework.ui.Model
import kotlin.math.max
import kotlin.math.min

object PageUtils {

    fun getRequestPageable(pageable: Pageable?): Pageable = pageable ?: Pageable.ofSize(20)

    fun getRequestModel(model: Model?): Model = model ?: ConcurrentModel()

    fun getPagesCount(current: Int, totalPages: Int): List<Int> {
        val pageNumbers = mutableListOf<Int>()
        val start = max(current - 3, 0)
        val end = min(totalPages, start + 9)
        pageNumbers.add(0)
        for (i in start until end) {
            if (i != 0 && i != totalPages - 1) {
                pageNumbers.add(i)
            }
        }
        pageNumbers.add(totalPages - 1)
        return pageNumbers
    }
}