package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.domain.image.ImageService
import com.starskvim.printmodelsarchive.persistance.MinioDataService
import org.springframework.stereotype.Service
import java.io.File

@Service
class MinioService(

    private val minioDataService: MinioDataService,
    private val imageService: ImageService

) {
    @LoggTime
    suspend fun saveImageWithCompressing(file: File, storageName: String) {
        val baos = imageService.getCompressedImgFromDisk(file.path, 0.2f) // TODO config
        val bytes = baos.toByteArray()
        minioDataService.savePrintModelImage(storageName, bytes.size.toLong(), bytes.inputStream())
    }
}