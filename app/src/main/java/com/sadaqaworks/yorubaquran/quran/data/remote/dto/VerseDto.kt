package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VerseDto(
    @SerialName("_id")
    val id: Int,
    val surahId:Int,
    val verseId:Int,
    val arabic:String,
    val translation: String,
    val footnote:String?,

)
