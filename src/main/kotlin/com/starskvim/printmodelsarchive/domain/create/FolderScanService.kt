package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import mu.KLogging
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
    fun getFilesFromDisk(): MutableCollection<File> {
        val start = System.currentTimeMillis()
        val files = File(addressFigure).walk().toMutableList()
        files.addAll(File(addressOther).walk().toList())
        files.addAll(File(addressPack).walk().toList())
        val end = System.currentTimeMillis()
        logger.info { "ScanRepository SIZE ${files.size}, TIME ${end - start}" }
        return files
    }

    companion object : KLogging()
}