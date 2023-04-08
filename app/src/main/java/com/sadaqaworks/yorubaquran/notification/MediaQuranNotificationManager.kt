package com.sadaqaworks.yorubaquran.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat.Token
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.sadaqaworks.yorubaquran.MainActivity
import com.sadaqaworks.yorubaquran.audio.AudioService
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadNotification
import com.sadaqaworks.yorubaquran.R

class MediaQuranNotificationManager(private val audioService: AudioService, private val sessionToken: Token) :
    QuranNotificationManager(audioService) {

    private var playAction: NotificationCompat.Action
  //private var stopAction: NotificationCompat.Action
    private  var pauseAction: NotificationCompat.Action
    private var nextAction: NotificationCompat.Action
    private var previousAction: NotificationCompat.Action
    //private var notificationManager: NotificationManager
    private var verseId: Int? = null

    companion object {
        const val  CHANNEL_ID = "audioChannelId"
        const val REQUEST_CODE = 501
        const val NOTIFICATION_ID = 200
    }

    init {
       // notificationManager = audioService.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        playAction = NotificationCompat.Action(
            android.R.drawable.ic_media_play,
            audioService.getString(R.string.play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                audioService,
                PlaybackStateCompat.ACTION_PLAY
            )
        )
        pauseAction = NotificationCompat.Action(
            android.R.drawable.ic_media_pause,
            audioService.getString(R.string.pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                audioService,
                PlaybackStateCompat.ACTION_PAUSE
            )
        )

        nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next,
            audioService.getString(R.string.next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                audioService,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )
        previousAction = NotificationCompat.Action(
            android.R.drawable.ic_media_previous,
            audioService.getString(R.string.previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                audioService,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )
        notificationManager.cancelAll()
    }
    fun setVerseId(verseId:Int){
       this.verseId = verseId
    }


    override fun getNotification(
        state: PlaybackStateCompat?,
        preparedMedia: MediaMetadataCompat?,
        downloadNotification: DownloadNotification?
    ): Notification {

        val isPlaying = state?.state!! == PlaybackStateCompat.STATE_PLAYING

        return  buildNotification( isPlaying,preparedMedia,null).build()
    }


    override fun buildNotification(
        isPlaying: Boolean?,
        preparedMedia: MediaMetadataCompat?,
        downloadNotification: DownloadNotification?
    ): NotificationCompat.Builder {

       val description = "Enable For Playing of Quran Audio in the background"
       val name =  "Quran Audio"
       // if it is android 0 or higher
        if (android0orHigher()){
            // build notification channel
            createChannel(
                CHANNEL_ID,
                description,
                name
            )
        }
        val remoteView = buildRemoteView(isPlaying!!, preparedMedia!!)
       val builder : NotificationCompat.Builder = NotificationCompat.Builder(audioService, CHANNEL_ID)
       builder.apply {
           color = audioService.getColor(R.color.deep_green)
           setContent(remoteView)
            setContentIntent(createContentIntent())
           setSmallIcon(R.drawable.mecca)
           setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
               audioService, PlaybackStateCompat.ACTION_STOP
           )
           )
           setStyle(NotificationCompat.DecoratedCustomViewStyle())
           setSilent(true)
           setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
       }
        return  builder

    }

    override fun buildRemoteView(
        isPlaying:Boolean,
        preparedMedia: MediaMetadataCompat
    ):RemoteViews{
        val surahName = preparedMedia.description.title
        val surahId = preparedMedia.getLong("surahId")
        val initialVerseId = preparedMedia.getLong("numberInSurah").toInt()
        val remoteView = RemoteViews(audioService.packageName, R.layout.music_background)
        if (isPlaying){
            remoteView.setImageViewResource(R.id.play_icon, R.drawable.notification_pause)
        }
        else{
            remoteView.setImageViewResource(R.id.play_icon, R.drawable.notification_play)

        }
        val surahCountText = "Surah: $surahId Verse: ${verseId?:initialVerseId}"
        remoteView.setTextViewText(R.id.notification_surah_name, surahName)
        remoteView.setTextViewText(R.id.notification_surah_count, surahCountText)
        val playPendingIntent =  MediaButtonReceiver.buildMediaButtonPendingIntent(
            audioService,
            PlaybackStateCompat.ACTION_PLAY
        )
        val pausePendingIntent =  MediaButtonReceiver.buildMediaButtonPendingIntent(
            audioService,
            PlaybackStateCompat.ACTION_PAUSE
        )

        val nextPendingIntent  = MediaButtonReceiver.buildMediaButtonPendingIntent(
            audioService,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
        val previousPendingIntent  = MediaButtonReceiver.buildMediaButtonPendingIntent(
            audioService,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
        val playOrPauseIntent = if (isPlaying)pausePendingIntent else playPendingIntent
        remoteView.setOnClickPendingIntent(R.id.play_icon,playOrPauseIntent)
        remoteView.setOnClickPendingIntent(R.id.next_icon,nextPendingIntent)
        remoteView.setOnClickPendingIntent(R.id.previous_icon,previousPendingIntent)
        return remoteView

    }

    private fun createContentIntent():PendingIntent{

        val openMainActivity = Intent(audioService,MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivities(audioService, REQUEST_CODE,
            arrayOf(openMainActivity),PendingIntent.FLAG_CANCEL_CURRENT)
    }
    // Check if android oreo or higher



}