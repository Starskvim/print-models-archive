package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.config.ArchiveConfiguration
import com.starskvim.print.models.archive.domain.files.FolderService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.downloads.PrintModelDownloads
import com.starskvim.print.models.archive.persistance.repository.PrintModelDownloadsRepository
import com.starskvim.print.models.archive.utils.Constants.Data.ADMIN_DOWNLOADS_ID
import com.starskvim.print.models.archive.utils.Constants.Logs.UN_ER
import com.starskvim.print.models.archive.utils.WrapUtils.logErrorIfFail
import com.starskvim.print.models.archive.utils.WrapUtils.wrapException
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogging
import org.springframework.stereotype.Service
import ru.starskvim.inrastructure.webflux.advice.exception.DomainException
import ru.starskvim.inrastructure.webflux.advice.exception.NotFoundException
import java.io.File

@Service
class PrintModelDownloadsService(
    private val folderService: FolderService,
    private val dataService: PrintModelDataService,
    private val repository: PrintModelDownloadsRepository,
    private val config: ArchiveConfiguration
) {

    suspend fun addToDownloads(id: String) {
        repository.findById(ADMIN_DOWNLOADS_ID).awaitSingleOrNull()
            ?: initDownloads()
                .apply { ids.add(id) }
                .also { repository.save(it).awaitSingle() }
    }

    suspend fun downloadById(id: String) {
        val model = dataService.getPrintModelById(id)
            ?: throw NotFoundException("$UN_ER Noy found for download id $id")
        val sourcePath = model.path ?: throw DomainException("$UN_ER Invalid model path id $id")
        val targetPath = config.tempFolderPath + model.folderName
        folderService.copyDirectoryAsync(
            source = File(sourcePath),
            target = File(targetPath),
        )
    }

    suspend fun downloadAll() {
        val downloads = repository.findById(ADMIN_DOWNLOADS_ID).awaitSingleOrNull()
            ?: initDownloads()
        downloads.apply {
            ids.map { wrapException(it) { downloadById(it) } }
                .forEach {
                    it.logErrorIfFail(
                        "$UN_ER Download model with id ${it.source}.",
                        logger
                    )
                }
            ids.clear()
        }
        repository.save(downloads)
            .awaitSingleOrNull()
    }

    private suspend fun initDownloads(): PrintModelDownloads {
        return repository.save(
            PrintModelDownloads(
                ADMIN_DOWNLOADS_ID,
                mutableListOf()
            )
        ).awaitSingle()
    }

    companion object : KLogging()
}