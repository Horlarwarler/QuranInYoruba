package com.sadaqaworks.yorubaquran.dua.presentation.chapter

import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel

data class DuaChaptersUiState(

    val chapters: List<DuaChapterModel> = emptyList(),
    val isLoading: Boolean = false,
    val messages: List<String> = emptyList(),
    val title: String = "Dua Chapter"
)
