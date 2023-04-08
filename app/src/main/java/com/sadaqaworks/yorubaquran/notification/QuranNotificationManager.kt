package com.sadaqaworks.yorubaquran.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadNotification

abstract class QuranNotificationManager(private val context:Context) {
    protected var notificationManager: NotificationManager

    init {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(
        channelId:String,
        description:String,
        name:String
     ){

        if (notificationManager.getNotificationChannel(channelId) != null){
            return
        }
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(
           channelId,name, importance
        )
        notificationChannel.apply {
            setDescription(description)
            enableLights(true)
            lightColor = Color.GREEN

        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    protected fun android0orHigher():Boolean{

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

//    private fun createContentIntent(): PendingIntent {
//
//        val openMainActivity = Intent(context, MainActivity::class.java)
//        openMainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        return PendingIntent.getActivities(context, MediaQuranNotificationManager.REQUEST_CODE,
//            arrayOf(openMainActivity), PendingIntent.FLAG_CANCEL_CURRENT)
//    }
    protected abstract fun buildNotification(
        isPlaying:Boolean? = null,
        preparedMedia: MediaMetadataCompat? = null,
        downloadNotification: DownloadNotification? = null
): NotificationCompat.Builder

    abstract fun getNotification(
        state: PlaybackStateCompat? = null,
        preparedMedia: MediaMetadataCompat? = null ,
        downloadNotification: DownloadNotification? = null


    ): Notification

    protected abstract fun buildRemoteView(
        isPlaying:Boolean,
        preparedMedia: MediaMetadataCompat
    ): RemoteViews?

    fun notificationManager():NotificationManager{

        return  notificationManager
    }
}