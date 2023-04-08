package com.sadaqaworks.yorubaquran.quran.domain.model

data class Reciter(
    val identifier: String,
    val language: String = "ar",
    val name:String,
    val englishName:String
)
