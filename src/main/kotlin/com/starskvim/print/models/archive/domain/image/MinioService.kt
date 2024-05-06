package com.starskvim.print.models.archive.domain.image

import com.starskvim.print.models.archive.persistance.MinioDataService
import org.springframework.stereotype.Service
import java.io.File

@Service
class MinioService(
    private val minioDataService: MinioDataService,
    private val imageService: ImageService
) {

    suspend fun saveImageWithCompressing(file: File, storageName: String) {
        try {
            val baos = imageService.getCompressedImgFromDisk(file.path, 0.8f) // TODO config
            val bytes = baos.toByteArray()
            minioDataService.savePrintModelImage(storageName, bytes.size.toLong(), bytes.inputStream())
        } catch (ex: Exception) {
            return
        }
    }
}