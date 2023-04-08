package com.sadaqaworks.yorubaquran.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager

abstract class PlayerAdapter() {

    lateinit var audioManager: AudioManager
    lateinit var audioFocusHelper:AudioFocusRequest

    companion object {

        private const val DEFAULT_VOLUME = 1F
        private const val DUCK_VOLUME = 0.2F


    }

    init {

    }

    private val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var audioBecomingNoisyRegistered = false
    abstract fun isPlaying():Boolean
   class broadcastReceiver(private val isplaying:Boolean, val play:() -> Unit) : BroadcastReceiver(){
       override fun onReceive(context: Context?, intent: Intent?) {
           if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action){
              if (isplaying){
                  play()
              }
           }
       }
   }




    abstract fun pause()

}