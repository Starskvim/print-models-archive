package com.starskvim.print.models.archive.rest.model.response

class ProgressResponse(

    private var totalCount: Int,
    private var currentCount: Int,
    private val currentTask: String

) {
    companion object {
        fun empty(): ProgressResponse = ProgressResponse(0, 0, "Empty")
    }
}