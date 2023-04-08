package com.sadaqaworks.yorubaquran.audio

import android.support.v4.media.session.PlaybackStateCompat

interface PlaybackInfoListener {
    fun playbackStataChanged(state : PlaybackStateCompat)
    fun onCompleted()
}