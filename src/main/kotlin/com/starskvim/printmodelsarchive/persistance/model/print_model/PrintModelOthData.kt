package com.starskvim.printmodelsarchive.persistance.model.print_model

data class PrintModelOthData(

    var parentFileName: String,
    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var storageName: String?

) {

    fun isImage(): Boolean = storageName != null
}