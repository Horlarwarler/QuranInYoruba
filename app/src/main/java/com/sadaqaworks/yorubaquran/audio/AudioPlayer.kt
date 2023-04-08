package com.sadaqaworks.yorubaquran.audio

import android.accounts.NetworkErrorException
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.util.CustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL


class AudioPlayer  (
    private val  context: Context,
    private val  listener: PlaybackInfoListener
) {

    private var isPlaying = false


    var mediaPlayer: MediaPlayer? = null


    private var surahFinishedPlaying:Boolean = false

    private  var currentState: Int = PlaybackStateCompat.STATE_NONE

    init {
        init()
    }
    private fun init(){
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }


    fun prepareAudioFile(datasource:Uri) : Boolean{

        mediaPlayer?:run {
            init()
        }

        val coroutineScope  = CoroutineScope(Dispatchers.IO)
        val isUrl = isUrl(datasource.toString())
        try {
            if (isUrl)  mediaPlayer?.setDataSource(datasource.toString()) else   mediaPlayer?.setDataSource(context, datasource)
            return  true
        }
        catch (error:IllegalStateException){

            showToastError("Music Already Playing")
        }
        catch (error: SecurityException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Internet Connection is lost")

        }
        catch (error :IOException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Internet Connection is lost or File is currupted")
        }

        catch (error: IllegalArgumentException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Bad UrL")
        }
        catch (error:Exception){
            showToastError("Error is here")
        }
        return false

    }
    private fun isUrl(datasource: String):Boolean{
        return try {
            URL(datasource).toURI()
            true

        } catch (e:MalformedURLException){
            false
        } catch (e:URISyntaxException){
            false
        }
    }




    fun play(){

        try {
            mediaPlayer?.apply {
                setOnPreparedListener {

                    start()
                    if (currentState != PlaybackStateCompat.STATE_PLAYING){
                        setNewState(PlaybackStateCompat.STATE_PLAYING)
                    }

                }
                prepareAsync()
            }

        }
        catch (error:IllegalStateException){

            showToastError("Music Already Playing")
        }
        catch (error: NetworkErrorException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Internet Connection is lost")

        }
        catch (error :IOException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Internet Connection is lost or File is currupted")
        }

        catch (error: IllegalArgumentException){
            setNewState(PlaybackStateCompat.STATE_PAUSED)
            showToastError("Bad UrL")
        }

    }

    fun pause(){
        mediaPlayer?.pause()
        setNewState(PlaybackStateCompat.STATE_PAUSED)
        isPlaying = false
    }

    fun resume(){
        mediaPlayer?.start()
        isPlaying = true
        setNewState(PlaybackStateCompat.STATE_PLAYING)

    }

    fun stop(isFinallyStopped:Boolean=false){
     //   setNewState(PlaybackStateCompat.STATE_STOPPED)
        if (isFinallyStopped){
            setNewState(PlaybackStateCompat.STATE_STOPPED)
        }
        isPlaying = false
        mediaPlayer?.stop()
        release()

    }

   private fun release(){
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setNewState(@PlaybackStateCompat.State newState: Int){

        if (currentState == newState){
            return
        }
        currentState = newState
        if (newState == PlaybackStateCompat.STATE_STOPPED){
            surahFinishedPlaying = true
        }
        val reportPosition = mediaPlayer?.currentPosition ?: 0
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(newState,
            reportPosition.toLong(),
            1.0f,
            SystemClock.elapsedRealtime());
        listener.playbackStataChanged(stateBuilder.build())
    }


    private fun getAvailableActions(): Long {
        var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        actions = when (currentState) {
            PlaybackStateCompat.STATE_STOPPED -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE)
            PlaybackStateCompat.STATE_PLAYING -> actions or (PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO)
            PlaybackStateCompat.STATE_PAUSED -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP)
            else -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE)
        }
        return actions
    }


    private fun showToastError(errorMessage:String){
        val customToast = CustomToast(context)
        customToast.showToast(
            errorMessage, R.drawable.custom_toast_yellow_background, R.drawable.baseline_error_24
        )
    }





}