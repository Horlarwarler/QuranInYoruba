package com.sadaqaworks.yorubaquran.quran.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable

data class SurahAudioDataDto(
    @SerialName("ayahs")
    val verses:List<VerseAudioDataDto>
)
