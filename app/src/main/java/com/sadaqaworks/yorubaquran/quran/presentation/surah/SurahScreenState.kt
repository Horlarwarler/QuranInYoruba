package com.sadaqaworks.yorubaquran.quran.presentation.surah

import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails

data class SurahScreenState(
    val isLoading: Boolean = true,
    val allSurah:List<SurahDetails> = emptyList(),
)
