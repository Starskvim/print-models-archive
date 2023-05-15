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
    suspend fun saveImage(file: File, storageName: String) { // TODO compressing
        minioDataService.saveObject(file, storageName)
    }
}