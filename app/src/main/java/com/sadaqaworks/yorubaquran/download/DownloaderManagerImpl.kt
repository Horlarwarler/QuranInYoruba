package com.sadaqaworks.yorubaquran.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class DownloaderManagerImpl(
    private val context: Context
) : DownloaderInterface {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override suspend fun downloadFile(fileUrl: String, fileName: String): Long {
        val request = DownloadManager.Request(fileUrl.toUri())
            .setMimeType("Audio/mp3")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context,Environment.DIRECTORY_MUSIC,"audio.mp3")
            .setAllowedOverMetered(true)
            .setTitle(fileName)
        return downloadManager.enqueue(request)


    }

}