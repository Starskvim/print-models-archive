package com.starskvim.print.models.archive.domain.files

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.withContext
import mu.KLogging
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Service
class FolderService(
    private val dispatcher: ExecutorCoroutineDispatcher
) {

    suspend fun main1() {
        val sourceDir = File("путь/к/исходной/папке")
        val targetDir = File("путь/к/целевой/папке")

        try {
            copyDirectoryAsync(sourceDir, targetDir)
            println("Папка успешно скопирована!")
        } catch (e: Exception) {
            println("Ошибка при копировании папки: ${e.message}")
        }
    }

    suspend fun copyDirectoryAsync(
        source: File,
        target: File
    ): Unit = withContext(dispatcher) {
        if (!target.exists()) {
            target.mkdirs()
        }
        source.listFiles()?.forEach { file ->
            val targetFile = File(target, file.name)
            if (file.isDirectory) {
                copyDirectoryAsync(file, targetFile)
            } else {
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    companion object : KLogging()
}