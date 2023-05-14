package com.starskvim.printmodelsarchive.domain.image

import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.swing.ImageIcon


@Service
class ImageService {

    suspend fun getBase64Img(path: String?, isNeedCompression: Boolean, quality: Float): String {
        if (path == null) {
            return ""
        }
        return if (isNeedCompression) {
            getCompressedImgFromDisk(path, quality)
        } else {
            getFullImgFromDisk(path)
        }
    }

    private suspend fun getFullImgFromDisk(path: String): String {
        val inputStreamReader = FileInputStream(path)
        val file = File(path)
        val bytes = ByteArray(file.length().toInt())
        inputStreamReader.read(bytes)
        return String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8)
    }

    private suspend fun getCompressedImgFromDisk(path: String, quality: Float): String {
        val input = File(path)
        val image = ImageIO.read(input)
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
        return String(Base64.encodeBase64(baos.toByteArray()), StandardCharsets.UTF_8)
    }
}