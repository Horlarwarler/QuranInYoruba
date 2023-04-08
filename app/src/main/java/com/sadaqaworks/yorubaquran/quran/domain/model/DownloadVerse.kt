package com.sadaqaworks.yorubaquran.quran.domain.model

import android.net.Uri

data class DownloadVerse(
    val id:Int,
    val uri: Uri,
    val surahName:String? = null,
    val surahId:Int? = null,
    val numberInSurah: Int? = null
)
