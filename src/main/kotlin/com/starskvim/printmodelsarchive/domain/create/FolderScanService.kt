package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import mu.KLogging
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File


@Service
class FolderScanService(

    @Value("\${scan.address1}")
    private val addressFigure: String,
    @Value("\${scan.address2}")
    private val addressOther: String,
    @Value("\${scan.address3}")
    private val addressPack: String,

    ) {

    @LoggTime
    fun getFilesFromDisk(): Collection<File> {
        val start = System.currentTimeMillis()
        val files = mutableListOf<File>()
        files.addAll(FileUtils.streamFiles(File(addressFigure), true, null).toList())
        files.addAll(FileUtils.streamFiles(File(addressOther), true, null).toList())
        files.addAll(FileUtils.streamFiles(File(addressPack), true, null).toList())
        val end = System.currentTimeMillis()
        logger.info { "ScanRepository SIZE ${files.size}, TIME ${end - start}" }
        return files
    }

    companion object : KLogging()
}