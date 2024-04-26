package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.create.CreatePrintModelService
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import com.starskvim.print.models.archive.utils.Constants.Url.CHECK_FOLDERS
import com.starskvim.print.models.archive.utils.Constants.Url.CLEAR_ARCHIVE
import com.starskvim.print.models.archive.utils.Constants.Url.CREATE_ARCHIVE
import com.starskvim.print.models.archive.utils.Constants.Url.GET_PROGRESS_TASK
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class ArchiveAdminApiController(
    private val createService: CreatePrintModelService,
    private val taskProgressService: TaskProgressService
) {

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(CREATE_ARCHIVE)
    suspend fun createArchive() = coroutineScope { launch { createService.initializeArchive() } }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(CHECK_FOLDERS)
    suspend fun checkFolders() = createService.checkFolders()

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(GET_PROGRESS_TASK)
    suspend fun getProgressTask() = taskProgressService.getProgressTask(INITIALIZE_ARCHIVE_TASK)

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(CLEAR_ARCHIVE)
    suspend fun clearArchive() = createService.clearArchive()
}