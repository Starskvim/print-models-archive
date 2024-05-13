package com.starskvim.print.models.archive.domain.image

import com.starskvim.print.models.archive.persistance.MinioDataService
import com.starskvim.print.models.archive.utils.Constants.Bucket.PRINT_MODEL_IMAGE
import com.starskvim.print.models.archive.utils.WrapUtils.logErrorIfFail
import com.starskvim.print.models.archive.utils.WrapUtils.wrapException
import mu.KLogging
import org.springframework.stereotype.Service
import ru.starskvim.inrastructure.webflux.utils.constant.Logging.UNEXPECTED_ERROR
import java.io.File

@Service
class MinioService(
    private val minioDataService: MinioDataService,
    private val imageService: ImageService
) {

    suspend fun saveImageWithCompressing(file: File, storageName: String) {
        if (minioDataService.isImageNotExist(storageName)) {
            val opResultBaos = wrapException(file) {
                imageService.getCompressedImgFromDisk(file.path, 0.8f) // TODO config
            }.logErrorIfFail(
                "$UNEXPECTED_ERROR With file image ${file.path}",
                logger
            ).result ?: return
            val bytes = opResultBaos.toByteArray()
            minioDataService.savePrintModelImage(storageName, bytes.size.toLong(), bytes.inputStream())
        }
    }

    suspend fun recreateBucket() {
        if (minioDataService.isBucketExist(PRINT_MODEL_IMAGE)) {
            minioDataService.deleteBucket(PRINT_MODEL_IMAGE)
            minioDataService.createBucket(PRINT_MODEL_IMAGE)
        }
    }

    companion object : KLogging()
}