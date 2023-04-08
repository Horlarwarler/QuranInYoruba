package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class SurahDownloadDto(
    val surahName:String,
    val verses:List<VerseDownloadDto>
)
