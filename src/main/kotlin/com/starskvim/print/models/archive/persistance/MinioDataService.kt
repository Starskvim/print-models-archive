package com.starskvim.print.models.archive.persistance

import com.starskvim.print.models.archive.aop.LoggTime
import com.starskvim.print.models.archive.utils.Constants.Bucket.PRINT_MODEL_IMAGE
import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.errors.MinioException
import io.minio.http.Method
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets


@Service
class MinioDataService(
    private val client: MinioClient
) {

    @LoggTime
    suspend fun saveObject(file: File, name: String) {
        try {
            val res = client.putObject(
                PutObjectArgs.builder()
                    .bucket(PRINT_MODEL_IMAGE)
                    .`object`(name)
                    .stream(file.inputStream(), file.length(), -1)
                    .build()
            )
            println(res)
        } catch (ex: IOException) {
            throw IllegalStateException(ex.message)
        } catch (ex: MinioException) {
            throw IllegalStateException(ex.message)
        }
    }

    suspend fun savePrintModelImage(imageName: String, size: Long, image: InputStream) {
        try {
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(PRINT_MODEL_IMAGE)
                    .`object`(imageName)
                    .stream(image, size, -1)
                    .build()
            )
        } catch (ex: IOException) {
            throw IllegalStateException(ex.message)
        } catch (ex: MinioException) {
            throw IllegalStateException(ex.message)
        }
    }

    suspend fun getPrintModelImage(imageName: String): String {
        try {
            val res = client.getObject(
                GetObjectArgs.builder()
                    .bucket(PRINT_MODEL_IMAGE)
                    .`object`(imageName)
                    .build()
            )
            return String(Base64.encodeBase64(res.readAllBytes()), StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw IllegalStateException(ex.message)
        } catch (ex: MinioException) {
            throw IllegalStateException(ex.message)
        }
    }

    suspend fun getPrintModelImageUrl(imageName: String): String {
        try {
            return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(PRINT_MODEL_IMAGE)
                    .`object`(imageName)
                    .method(Method.GET)
                    .build()
            )
        } catch (ex: IOException) {
            throw IllegalStateException(ex.message)
        } catch (ex: MinioException) {
            throw IllegalStateException(ex.message)
        }
    }
}