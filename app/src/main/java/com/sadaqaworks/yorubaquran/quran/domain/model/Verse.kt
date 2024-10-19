package com.sadaqaworks.yorubaquran.quran.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val id: Int = 0,
    val surahId:Int = 0,
    val verseId:Int = 0,
    val arabic:String = "",
    val translation: String = "",
    val footnote:String? = "",
    val isBookmarked:Boolean = false

)
