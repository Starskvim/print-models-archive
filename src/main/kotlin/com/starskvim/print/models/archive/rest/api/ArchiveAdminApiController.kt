package com.starskvim.print.models.archive.rest.api

import com.starskvim.print.models.archive.domain.print_model.PrintModelAdminService
import com.starskvim.print.models.archive.domain.print_model.PrintModelDownloadsService
import com.starskvim.print.models.archive.domain.progress.TaskProgressService
import com.starskvim.print.models.archive.utils.Constants.Task.INITIALIZE_ARCHIVE_TASK
import com.starskvim.print.models.archive.utils.Constants.Url.CHECK_FOLDERS
import com.starskvim.print.models.archive.utils.Constants.Url.CLEAR_ARCHIVE
import com.starskvim.print.models.archive.utils.Constants.Url.CREATE_ARCHIVE
import com.starskvim.print.models.archive.utils.Constants.Url.GET_PROGRESS_TASK
import com.starskvim.print.models.archive.utils.Constants.Url.RECREATE_BUCKET
import com.starskvim.print.models.archive.utils.Constants.Url.UPDATE_ARCHIVE
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class ArchiveAdminApiController(
    private val adminService: PrintModelAdminService,
    private val taskProgressService: TaskProgressService,
    private val downloadsService: PrintModelDownloadsService,
) {

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(CREATE_ARCHIVE)
    suspend fun createArchive() = adminService.initializeArchive()

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(UPDATE_ARCHIVE)
    suspend fun updateArchive() = adminService.updateArchive()

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(CHECK_FOLDERS)
    suspend fun checkFolders() = adminService.checkFolders()

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(GET_PROGRESS_TASK)
    suspend fun getProgressTask() = taskProgressService.getProgressTask(INITIALIZE_ARCHIVE_TASK)

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(CLEAR_ARCHIVE)
    suspend fun clearArchive() = adminService.clearArchive()

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(RECREATE_BUCKET)
    suspend fun recreateBucket() = adminService.recreateBucket()

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/download/{id}")
    suspend fun addDownloadModel(@PathVariable id: String) = downloadsService.addToDownloads(id)

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/download/{id}")
    suspend fun downloadModel(@PathVariable id: String) = downloadsService.downloadById(id)

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/download")
    suspend fun downloadModels() = downloadsService.downloadAll()
}