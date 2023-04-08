package com.sadaqaworks.yorubaquran.audio

import android.support.v4.media.MediaBrowserCompat

data class AudioServiceState(
    val audios:List<MediaBrowserCompat.MediaItem> = emptyList()
)
