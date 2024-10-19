package com.sadaqaworks.yorubaquran.download

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sadaqaworks.yorubaquran.notification.DownloadNotificationManager
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.SurahDownloadDto
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadNotification
import com.sadaqaworks.yorubaquran.util.CustomError
import com.sadaqaworks.yorubaquran.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService () : Service() {
    lateinit var downloadNotificationManager:DownloadNotificationManager
     lateinit var surahName: String
    private var surahToDownload:SurahDownloadDto? = null
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var downloadState:DownloadState = DownloadState.START_DOWNLOAD
    @Inject
     lateinit var downloaderInterface: DownloaderInterface

    companion object{
        const val  ACTION_COMPLETE = "com.crezent.quraninyoruba.download_complete"
    }

    override fun onCreate() {
        super.onCreate()
        downloadNotificationManager = DownloadNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val surahJson = intent?.getStringExtra("surah_download")?:return START_STICKY_COMPATIBILITY
        Log.d("TAG","START SERVICE $surahJson")

        surahToDownload = Json.decodeFromString<SurahDownloadDto>( surahJson)
        surahName = surahToDownload!!.surahName
        if (surahToDownload!!.verses.isNotEmpty()){
            startDownload()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startDownload(){
        val intent = Intent(ACTION_COMPLETE)
        downloadState = DownloadState.DOWNLOADING
        val verses = surahToDownload!!.verses
        coroutineScope.launch {
            try {
                setNotification()
                for (index in verses.indices){
                    val verse = verses[index]
                     downloaderInterface.downloadFile(verse.fileUrl, verse.fileName)
                    val progress = (index+1) * 100 / verses.size
                    setNotification(progress = progress)
                    intent.putExtra("isSuccessfully",true)
                }
                downloadState = DownloadState.FINISH_DOWNLOAD
                setNotification()
            }
            catch (error:CustomError){
                downloadState = DownloadState.ERROR_DOWNLOAD
                setNotification(errorMessage = error.message?:"Error")
                intent.putExtra("isSuccessfully",false)

            }
            catch (error:Exception){
                downloadState = DownloadState.ERROR_DOWNLOAD
                setNotification(errorMessage = error.message?:"Error")
                intent.putExtra("isSuccessfully",false)
            }
            finally {
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }

        }


    }

    private fun setNotification(errorMessage:String? = null, progress:Int? = null){
        when(downloadState){
            DownloadState.DOWNLOADING ->{
                val notificationModel = DownloadNotification(
                    title = "Download",
                    description = "Surah $surahName  is being downloaded ",
                    icon = R.drawable.baseline_downloading_24,
                    silent = true,
                    priority = NotificationCompat.PRIORITY_DEFAULT,
                    progress = progress
                )
                val notification = downloadNotificationManager.getNotification(downloadNotification = notificationModel)
                downloadNotificationManager.notificationManager().notify(0,notification)
            }
            DownloadState.ERROR_DOWNLOAD -> {
                val notificationModel = DownloadNotification(
                    title = "Download Error Occurs",
                    description = "Surah $surahName is not downloaded due to $errorMessage",
                    icon = R.drawable.baseline_error_24,
                    silent = false,
                    priority = NotificationCompat.PRIORITY_HIGH,
                )
                val notification = downloadNotificationManager.getNotification(downloadNotification = notificationModel)
                downloadNotificationManager.notificationManager().cancelAll()
                downloadNotificationManager.notificationManager().notify(0,notification)
            }

            DownloadState.FINISH_DOWNLOAD ->{
                val notificationModel = DownloadNotification(
                    title = "Surah Downloaded",
                    description = "Surah $surahName has been successfully downloaded",
                    icon = R.drawable.baseline_file_download_24,
                    silent = false,
                    priority = NotificationCompat.PRIORITY_LOW,
                )
                val notification = downloadNotificationManager.getNotification(downloadNotification = notificationModel)
                downloadNotificationManager.notificationManager().cancelAll()
                downloadNotificationManager.notificationManager().notify(0,notification)
            }
            else ->{

            }
        }
    }
    

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}