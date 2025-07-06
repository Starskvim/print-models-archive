package com.starskvim.print.models.archive.config.ai

import java.util.concurrent.atomic.AtomicInteger

class AiModel (

    val model: String,
    val limit: Int,
    val currentReqCount: AtomicInteger = AtomicInteger(-1)

) {

    fun canRequest() : Boolean {
        return (limit >= currentReqCount.get())
    }
    fun incrementRequest() {
        currentReqCount.incrementAndGet()
    }

}