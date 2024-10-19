package com.sadaqaworks.yorubaquran.quran.presentation.bookmark

import com.sadaqaworks.yorubaquran.quran.domain.model.Bookmark
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse

data class BookmarkUiState(
    val bookmarks: List<Verse > = emptyList(),
    val errorMessages: List<String> = emptyList(),
    val messages: List<String> = emptyList()
)
