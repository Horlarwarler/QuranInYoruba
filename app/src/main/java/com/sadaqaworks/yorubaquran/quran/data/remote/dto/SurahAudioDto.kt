package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SurahAudioDto(
    val code:Int,
    val status:String,
    val data: SurahAudioDataDto
)
