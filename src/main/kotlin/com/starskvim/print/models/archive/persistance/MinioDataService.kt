package com.starskvim.print.models.archive.persistance

import com.starskvim.print.models.archive.utils.Constants.Bucket.PRINT_MODEL_IMAGE
import com.starskvim.print.models.archive.utils.Constants.S3.BUCKET_POLICY
import io.minio.*
import io.minio.errors.ErrorResponseException
import io.minio.http.Method
import io.minio.messages.DeleteObject
import mu.KLogging
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import ru.starskvim.inrastructure.webflux.utils.constant.Logging.EXPECTED_ERROR
import java.io.InputStream
import java.nio.charset.StandardCharsets


@Service
class MinioDataService(
    private val client: MinioClient
) {

    suspend fun savePrintModelImage(imageName: String, size: Long, image: InputStream) {
        client.putObject(
            PutObjectArgs.builder()
                .bucket(PRINT_MODEL_IMAGE)
                .`object`(imageName)
                .stream(image, size, -1)
                .build()
        )
    }

    suspend fun isImageNotExist(imageName: String): Boolean {
        try {
            client.statObject(
                StatObjectArgs.builder()
                    .bucket(PRINT_MODEL_IMAGE)
                    .`object`(imageName)
                    .build()
            )
            logger.info { "$EXPECTED_ERROR File with name $imageName exist." }
            return false;
        } catch (e: ErrorResponseException) {
            return true;
        }
    }

    suspend fun createBucket(bucketName: String) {
        client.makeBucket(
            MakeBucketArgs
                .builder()
                .bucket(bucketName)
                .build()
        )
        client.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(bucketName)
                .config(BUCKET_POLICY)
                .build()
        )
    }

    suspend fun deleteBucket(bucketName: String) {
        val existObjects = client.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .build()
        )
        val objectsToDelete = mutableListOf<DeleteObject>()
        for (result in existObjects) {
            val item = result.get()
            objectsToDelete.add(
                DeleteObject(item.objectName())
            )
        }
        val results = client.removeObjects(
            RemoveObjectsArgs.builder()
                .bucket(bucketName)
                .objects(objectsToDelete)
                .build()
        )
        for (errorResult in results) {
            val error = errorResult.get()
        }
        client.removeBucket(
            RemoveBucketArgs
                .builder()
                .bucket(bucketName)
                .build()
        )
    }

    suspend fun isBucketExist(bucketName: String): Boolean {
        return client.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
        )
    }

    fun getPrintModelImage(imageName: String): String {
        val res = client.getObject(
            GetObjectArgs.builder()
                .bucket(PRINT_MODEL_IMAGE)
                .`object`(imageName)
                .build()
        )
        return String(Base64.encodeBase64(res.readAllBytes()), StandardCharsets.UTF_8)
    }

    suspend fun getPrintModelImageUrl(imageName: String): String {
        return client.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(PRINT_MODEL_IMAGE)
                .`object`(imageName)
                .method(Method.GET)
                .build()
        )
    }

    companion object : KLogging()
}