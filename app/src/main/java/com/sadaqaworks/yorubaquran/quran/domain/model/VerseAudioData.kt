package com.sadaqaworks.yorubaquran.quran.domain.model

data class VerseAudioData(
    val number:Int,
    val audio:String,
    val audioSecondary:List<String>,
    val numberInSurah:Int,
    val surahId:Int? = null,
    val surahName:String? = null
)
