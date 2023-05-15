package com.starskvim.printmodelsarchive.persistance.model

data class PrintModelOthData(

    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var storageName: String?
) {

    suspend fun isImage(): Boolean = storageName != null
}