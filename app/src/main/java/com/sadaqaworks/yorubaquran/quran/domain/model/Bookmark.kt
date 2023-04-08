package com.sadaqaworks.yorubaquran.quran.domain.model

data class Bookmark(
    val id: Int,
    val surahId: Int,
    val verseId:Int,
    val arabic:String,
    val translation:String,
    val footnote:String? = null
)
