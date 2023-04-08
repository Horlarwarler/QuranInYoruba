package com.sadaqaworks.yorubaquran.quran.presentation.bookmark

import com.sadaqaworks.yorubaquran.quran.domain.model.Bookmark

data class BookmarkUiState(
    val bookmarks: List<Bookmark > = emptyList(),
    val errorMessages: List<String> = emptyList(),
    val messages: List<String> = emptyList()
)
