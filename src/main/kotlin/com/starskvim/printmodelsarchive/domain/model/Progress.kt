package com.starskvim.printmodelsarchive.domain.model

import com.starskvim.printmodelsarchive.rest.model.response.ProgressResponse
import java.util.concurrent.atomic.AtomicInteger

data class Progress(

    var totalCount: Int,
    var currentCount: AtomicInteger,
    var currentTask: String

) {

    companion object {
        fun empty(): Progress = Progress(0, AtomicInteger(0), "Empty")
        fun Progress.toView(): ProgressResponse = ProgressResponse(
            this.totalCount,
            this.currentCount.get(),
            this.currentTask
        )
    }
}