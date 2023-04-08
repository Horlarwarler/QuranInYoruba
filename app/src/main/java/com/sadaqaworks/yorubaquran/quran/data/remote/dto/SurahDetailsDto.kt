package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class SurahDetailsDto(
    val id: Int,
    val surahId:Int,
    val type:String,
    val ayahNumber:Int,
    val arabic:String,
    val translation: String
)
