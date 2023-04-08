package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable

data class VerseAudioDataDto(
    val number:Int,
    val audio:String,
    val audioSecondary:List<String>,
    val numberInSurah:Int
)
