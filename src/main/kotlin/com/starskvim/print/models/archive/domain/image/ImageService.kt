package com.starskvim.print.models.archive.domain.image

import com.starskvim.print.models.archive.config.s3.MinioConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogging
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.swing.ImageIcon


@Service
class ImageService(
    private val minioConfig: MinioConfiguration
) {

    suspend fun getUrlForImage(fileName: String): String {
        return minioConfig.external + "/print-model-image/" + fileName
    }

    suspend fun getBucketForImage(fileName: String): String {
        return "/print-model-image/$fileName"
    }

    suspend fun getBase64Img(path: String?, isNeedCompression: Boolean, quality: Float): String {
        if (path == null) {
            return ""
        }
        return if (isNeedCompression) {
            val baos = getCompressedImgFromDisk(path, quality)
            return String(Base64.encodeBase64(baos.toByteArray()), StandardCharsets.UTF_8)
        } else {
            getFullImgFromDisk(path)
        }
    }

    // TODO file -> string -> file ?
    suspend fun getCompressedImgFromDisk(path: String, quality: Float): ByteArrayOutputStream {
        val input = File(path)
        val image = ImageIO.read(input) // TODO thread starvation?
        val iter = ImageIO.getImageWritersByFormatName("JPG")
        val baos = ByteArrayOutputStream()
        if (iter.hasNext()) {
            val writer = iter.next()
            val iwp = writer.defaultWriteParam
            iwp.compressionMode = ImageWriteParam.MODE_EXPLICIT
            iwp.compressionQuality = quality
            val mcios = MemoryCacheImageOutputStream(baos)
            writer.output = mcios
            val img2 = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
            val g = img2.graphics
            g.drawImage(ImageIcon(image).image, 0, 0, null)
            g.dispose()
            val iioimg = IIOImage(img2, null, null)
            try {
                writer.write(null, iioimg, iwp)
                mcios.close()
            } catch (ioe: IOException) {
                println(ioe.message)
            }
        }
        return baos
    }

    suspend fun readAndValidateLocalImage(imagePathString: String): Pair<ByteArray, String> =
        withContext(Dispatchers.IO) {
            val imagePath = try {
                Paths.get(imagePathString).normalize()
            } catch (e: InvalidPathException) {
                throw e
            }

            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                throw NoSuchFileException(imagePath.toString())
            }

            val mimeType = Files.probeContentType(imagePath)?.lowercase()
                ?: run {
                    log.warn { "Could not determine MIME type for path: $imagePath. Attempting default." }
                    "application/octet-stream"
                }

            if (mimeType !in SUPPORTED_MIME_TYPES) {
                val errorMsg =
                    "Unsupported image MIME type detected for file ${imagePath}: $mimeType. Supported: $SUPPORTED_MIME_TYPES"
                log.error(errorMsg)
                throw IllegalArgumentException(errorMsg)
            }
            log.debug("Detected MIME type: {}", mimeType)

            log.debug("Reading bytes from file: {}", imagePath)
            val imageBytes = Files.readAllBytes(imagePath)
            log.debug("Read {} bytes from file.", imageBytes.size)

            imageBytes to mimeType
        }

    private suspend fun getFullImgFromDisk(path: String): String {
        val inputStreamReader = FileInputStream(path)
        val file = File(path)
        val bytes = ByteArray(file.length().toInt())
        inputStreamReader.read(bytes)
        return String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8)
    }

    companion object {
        val log = KLogging().logger()
        private val SUPPORTED_MIME_TYPES =
            listOf("image/png", "image/jpeg", "image/webp", "image/heic", "image/heif")
    }
}
