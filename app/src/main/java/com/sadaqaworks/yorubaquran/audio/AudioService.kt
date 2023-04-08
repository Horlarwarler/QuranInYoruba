package com.sadaqaworks.yorubaquran.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.sadaqaworks.yorubaquran.notification.MediaQuranNotificationManager
import com.sadaqaworks.yorubaquran.util.convertMediaItem


class AudioService () : MediaBrowserServiceCompat() {

    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private val MY_MEDIA_ROOT_ID = "media_root_id"
    private val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    private lateinit var mediaSession: MediaSessionCompat
    private  var mediaItems: List<MediaBrowserCompat.MediaItem> = emptyList()
    lateinit var  audioPlayer: AudioPlayer
    var intentFilter:IntentFilter? = null
    private var playingIndex = 0
    private var serviceInStartedState :Boolean = false
    private var surahChanged:Boolean = false
    var preparedMediaSource: MediaMetadataCompat? = null
    lateinit var playbackStateCompat: PlaybackStateCompat


    @RequiresApi(Build.VERSION_CODES.O)
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Media Item", "Broadcast recieved")

            if (intent.action == "com.quran.media_items"){

                mediaItems = intent.getParcelableArrayListExtra("media_items")!!
                surahChanged = true
                notifyChildrenChanged(MY_MEDIA_ROOT_ID)
                callback.onPlay()

            }
        }

    }


    companion object AudioServiceObject{

        const val channelId = "audioChannel"
    }
    lateinit var builder:NotificationCompat.Builder
    lateinit var mediaNotificationManager: MediaQuranNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession,intent)
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        Log.d("AudioService","INitailling")
        mediaSession = MediaSessionCompat(this, "AudioService")
        mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.setCallback(callback)


        intentFilter = IntentFilter("com.quran.media_items")

        registerReceiver(receiver,intentFilter)

        sessionToken = mediaSession.sessionToken

        mediaNotificationManager = MediaQuranNotificationManager(this, sessionToken!!)

        val mediaListener = MediaPlayerListener()

        audioPlayer = AudioPlayer(this, mediaListener)
        mediaSession.isActive = true

    }
    @RequiresApi(Build.VERSION_CODES.O)

    private val callback = object: MediaSessionCompat.Callback() {

        var preparedDatasource: Uri? =null
        var isSkipped:Boolean = true


        override fun onPrepare() {

            Log.d("Session Token", "prepare is call")

            val size = mediaItems.size
            super.onPrepare()
            if (mediaItems.isEmpty()){
                // No data to play
                return
            }
            if (surahChanged){
                audioPlayer.stop()
                surahChanged = false
                playingIndex  = 0
            }
            val modIndex = try {
                playingIndex % size
            }
            catch (error:java.lang.ArithmeticException){
                0
            }
            catch (error:Exception){
                0
            }
            val currentItem = mediaItems[modIndex]
            preparedMediaSource = convertMediaItem(currentItem)
            preparedDatasource = mediaItems[modIndex].description.mediaUri
            val preparedAudioFileSuccess = audioPlayer.prepareAudioFile(preparedDatasource!!)
            if (!preparedAudioFileSuccess){
                if (isSkipped)playingIndex-- else playingIndex++
                return
            }
            mediaSession.setMetadata(preparedMediaSource)
            mediaNotificationManager.setVerseId(modIndex+1)


        }
        override fun onPlay() {

            if (preparedDatasource == null || surahChanged){
                onPrepare()
                Log.d("broadcast","Play Will play")
                audioPlayer.play()

            }
            else{
                audioPlayer.resume()
            }
            audioPlayer.mediaPlayer?.setOnCompletionListener {
                onSkipToNext()
            }

        }

        override fun onStop() {
            // stop the player (custom call)
            audioPlayer.stop()
            // Take the service out of the foreground
        }

        override fun onPause() {

            audioPlayer.pause()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()

            playingIndex++
            val size = mediaItems.size
            Log.d("Playing ", "$playingIndex  $size")
            isSkipped = true
            prepareAndPlay()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            playingIndex--
            isSkipped = false

            prepareAndPlay()

        }
        fun changePlayingVerse(state: PlaybackStateCompat){
            val notification = mediaNotificationManager.getNotification(
                state = state,
                preparedMedia = preparedMediaSource!!
            )
            mediaNotificationManager.notificationManager().notify(
                MediaQuranNotificationManager.NOTIFICATION_ID,
                notification
            )
        }


        private fun prepareAndPlay(){
            audioPlayer.stop()
            preparedDatasource = null
            onPlay()
            changePlayingVerse(playbackStateCompat)


        }
    }
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        audioPlayer .stop(isFinallyStopped = true)
        unregisterReceiver(receiver)
    }
    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        //  Browsing not allowed
        if (MY_EMPTY_MEDIA_ROOT_ID == parentMediaId) {
            Log.d("Empty", "Empty Second")
            result.sendResult(null)
            return
        }

        // Assume for example that the music catalog is already loaded/cached.
        Log.d("Empty", "MEDIA IS Empty${mediaItems.isEmpty()}")
        result.sendResult(mediaItems)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    inner class MediaPlayerListener : PlaybackInfoListener{
        private val serviceManager = ServiceManager()

       override fun playbackStataChanged(state: PlaybackStateCompat) {

           playbackStateCompat = state
           mediaSession.isActive = true

           mediaSession.setPlaybackState(state)
           when(state.state){
              PlaybackStateCompat.STATE_PLAYING ->{
                  serviceManager.moveServiceToStartedState(state)
             }
             PlaybackStateCompat.STATE_PAUSED ->{
                    serviceManager.updatePauseNotificationState(state)
             }
               PlaybackStateCompat.STATE_STOPPED ->{
                    serviceManager.moveServiceOutOfStartedState(state)
               }

           }
       }

       override fun onCompleted() {
           TODO("Not yet implemented")
       }
        inner class ServiceManager{

            fun moveServiceToStartedState(state: PlaybackStateCompat){
                val notification = mediaNotificationManager.getNotification(
                    state = state,
                    preparedMedia = preparedMediaSource!!
                )

                if (!serviceInStartedState){
                    ContextCompat.startForegroundService(this@AudioService, Intent(
                        this@AudioService,
                        AudioService::class.java
                    )
                    )
                    serviceInStartedState = true
                }

                startForeground(MediaQuranNotificationManager.NOTIFICATION_ID, notification)

            }


            fun updatePauseNotificationState(state: PlaybackStateCompat){
                stopForeground(false)
                val notification = mediaNotificationManager.getNotification(
                    state = state,
                    preparedMedia = preparedMediaSource!!,

                )
                mediaNotificationManager.notificationManager().notify(
                    MediaQuranNotificationManager.NOTIFICATION_ID,
                    notification
                )

            }

            fun moveServiceOutOfStartedState(state: PlaybackStateCompat?) {
                stopForeground(true)
                stopSelf()
                serviceInStartedState = false
            }


        }

    }


}