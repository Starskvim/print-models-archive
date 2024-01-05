package com.starskvim.printmodelsarchive.rest.api

import com.starskvim.printmodelsarchive.domain.TaskProgressService
import com.starskvim.printmodelsarchive.domain.create.CreatePrintModelService
import com.starskvim.printmodelsarchive.rest.model.response.ProgressResponse
import com.starskvim.printmodelsarchive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import com.starskvim.printmodelsarchive.utils.Constants.Url.CHECK_FOLDERS
import com.starskvim.printmodelsarchive.utils.Constants.Url.CREATE_ARCHIVE
import com.starskvim.printmodelsarchive.utils.Constants.Url.GET_PROGRESS_TASK
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/service")
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
    suspend fun getProgressTask(): ProgressResponse = taskProgressService.getProgressTask(INITIALIZE_ARCHIVE_TASK)
}