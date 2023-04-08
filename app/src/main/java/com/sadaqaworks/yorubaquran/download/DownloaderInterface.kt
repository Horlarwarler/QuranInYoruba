package com.sadaqaworks.yorubaquran.download


interface DownloaderInterface {
    suspend fun downloadFile(fileUrl:String, fileName:String):Long
}