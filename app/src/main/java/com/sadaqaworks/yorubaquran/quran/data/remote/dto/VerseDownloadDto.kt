package com.sadaqaworks.yorubaquran.quran.data.remote.dto


import kotlinx.serialization.Serializable

@Serializable
data class VerseDownloadDto(
    val fileUrl:String,
    val fileName:String
)
