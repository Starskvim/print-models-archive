package com.starskvim.printmodelsarchive.domain.create

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import io.minio.MinioClient
import io.minio.messages.Bucket
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class CreatePrintModelService(

    private val scanService: FolderScanService,
    private val dataService: PrintModelDataService,

    private val client: MinioClient

) {

    @LoggTime
    suspend fun initializeArchive() {
        val files = scanService.getFilesFromDisk()
        InitializeArchiveTask(dataService, files).execute()
    }

    @PostConstruct
    fun test() {
        val buckets: List<Bucket> = client.listBuckets()
        for (bucket in buckets) {
            println(bucket.name())
        }
    }
}