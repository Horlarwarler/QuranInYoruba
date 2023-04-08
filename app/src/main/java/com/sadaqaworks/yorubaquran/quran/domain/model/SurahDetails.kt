package com.sadaqaworks.yorubaquran.quran.domain.model

data class SurahDetails(
    val id: Int,
    val surahId:Int,
    val type:String,
    val ayahNumber:Int,
    val arabic:String,
    val translation: String
)

