package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.domain.model.Progress
import com.starskvim.printmodelsarchive.domain.model.Progress.Companion.toView
import com.starskvim.printmodelsarchive.rest.model.response.ProgressResponse
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TaskProgressService(
    private val progressHolder: ConcurrentHashMap<String, Progress> = ConcurrentHashMap<String, Progress>()
) {

    suspend fun getProgressTask(task: String): ProgressResponse {
        return (progressHolder[task] ?: Progress.empty()).toView()
    }

    suspend fun incrementTask(task: String, currentTask: String, total: Int) {
        if (progressHolder[task] == null) {
            progressHolder[task] = Progress.empty()
        }
        progressHolder[task]!!.apply {
            this.currentCount.getAndIncrement()
            this.totalCount = total
            this.currentTask = currentTask
        }
    }
}
