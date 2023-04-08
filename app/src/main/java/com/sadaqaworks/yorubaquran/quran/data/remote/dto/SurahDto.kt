package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SurahDto(
    @SerialName("quran")
    val verses: List<VerseDto>
)
