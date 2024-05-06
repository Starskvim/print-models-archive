package com.starskvim.print.models.archive.domain.create

import com.starskvim.print.models.archive.config.ArchiveConfiguration
import com.starskvim.print.models.archive.domain.image.MinioService
import com.starskvim.print.models.archive.domain.model.initialize.ArchiveTaskContext
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CreateArchiveProcessor(
    override val dataService: PrintModelDataService,
    override val minioService: MinioService,
    override val taskProgressService: TaskProgressService,
    override val config: ArchiveConfiguration,
    private val dispatcher: ExecutorCoroutineDispatcher,
) : AbstractArchiveProcessor(
    dataService,
    minioService,
    taskProgressService,
    config
) {

    override suspend fun typeTask() = INITIALIZE_ARCHIVE_TASK

    override suspend fun process(
        context: ArchiveTaskContext
    ): ArchiveTaskContext {
        context.apply { filesCount = files.size }
        val fileParts = context.files.chunked(config.coroutineBatch)
        coroutineScope {
            fileParts.forEach { part ->
                part.map {
                    async(dispatcher) { createModelsAndFiles(it, context, logger) }
                }.awaitAll()
            }
        }
        return postProcess(context)
    }

    companion object : KLogging()
}