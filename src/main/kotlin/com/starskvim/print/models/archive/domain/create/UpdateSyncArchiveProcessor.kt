package com.starskvim.print.models.archive.domain.create

import com.starskvim.print.models.archive.config.ArchiveConfiguration
import com.starskvim.print.models.archive.domain.image.MinioService
import com.starskvim.print.models.archive.domain.model.initialize.ArchiveTaskContext
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.utils.Constants.Task.UPDATE_ARCHIVE_TASK
import com.starskvim.print.models.archive.utils.CreateUtils.isNotArtefact
import com.starskvim.print.models.archive.utils.CreateUtils.isNotJson
import mu.KLogging
import org.springframework.stereotype.Service
import java.io.File

@Service
class UpdateSyncArchiveProcessor(
    override val dataService: PrintModelDataService,
    override val minioService: MinioService,
    override val taskProgressService: TaskProgressService,
    override val config: ArchiveConfiguration
) : AbstractArchiveProcessor(
    dataService,
    minioService,
    taskProgressService,
    config
) {

    override suspend fun typeTask() = UPDATE_ARCHIVE_TASK

    override suspend fun process(
        context: ArchiveTaskContext
    ): ArchiveTaskContext {
        val newFiles = filterInputFiles(context.files)
        if (newFiles.isEmpty()) {
            logger.info { "Update archive task. New models not found." }
            return context.apply { models = mutableListOf() }
        } else {
            logger.info { "Update archive task. New models found. ${newFiles.size}" }
            context.apply { filesCount = newFiles.size }
        }
        newFiles.forEach { createModelsAndFiles(it, context, logger) }
        return postProcess(context)
    }

    private suspend fun filterInputFiles(
        files: MutableCollection<File>
    ): List<File> {
        val savedModelFolders = dataService.resolveAllExistModelNames()
        return files.filter {
            savedModelFolders.contains(it.parentFile.name).not()
                    && isNotArtefact(it.parentFile.name)
                    && isNotJson(it)
        }
    }

    companion object : KLogging()
}