package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class VerseDto(
    val id: Int = 0,
    val surahId:Int = 0,
    val verseId:Int = 0,
    val arabic:String = "",
    val translation: String = "",
    val footnote:String? = "",
    )
