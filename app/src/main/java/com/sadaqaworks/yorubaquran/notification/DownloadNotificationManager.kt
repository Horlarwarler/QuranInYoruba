package com.sadaqaworks.yorubaquran.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.sadaqaworks.yorubaquran.MainActivity
import com.sadaqaworks.yorubaquran.download.DownloadService
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadNotification

class DownloadNotificationManager(private val downloadService:DownloadService) : QuranNotificationManager(downloadService) {
    companion object {
        const val CHANNEL_ID ="DownloadChannelId"
        const val NOTIFICATION_ID= 123

    }
    override  fun buildNotification(
        isPlaying: Boolean?,
        preparedMedia: MediaMetadataCompat?,
        downloadNotification: DownloadNotification?
    ): NotificationCompat.Builder {
        val description = "Show Download Progress"
        val name =  "Download Quran"
        // if it is android 0 or higher
        if (android0orHigher()){
            // build notification channel
            createChannel(
                CHANNEL_ID,
                description,
                name
            )
        }
        val notificationBuilder = NotificationCompat.Builder(downloadService, CHANNEL_ID)
        notificationBuilder.apply {
            setContentTitle(downloadNotification?.title!!)
            setContentText(downloadNotification.description)
            setSilent(downloadNotification.silent)
            setSmallIcon(downloadNotification.icon)
            setVisibility(VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(createContentIntent())
            if (downloadNotification.progress != null){
                setProgress(100, downloadNotification.progress,false)
            }

        }

        return notificationBuilder

    }

    private fun createContentIntent(): PendingIntent {

        val openMainActivity = Intent(downloadService, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivities(downloadService, MediaQuranNotificationManager.REQUEST_CODE,
            arrayOf(openMainActivity), PendingIntent.FLAG_CANCEL_CURRENT)
    }

    override fun getNotification(
        state: PlaybackStateCompat?,
        preparedMedia: MediaMetadataCompat?,
        downloadNotification: DownloadNotification?

    ): Notification {

        return  buildNotification(downloadNotification = downloadNotification).build()
    }

    override fun buildRemoteView(
        isPlaying: Boolean,
        preparedMedia: MediaMetadataCompat
    ): RemoteViews? {
       return  null
    }
}