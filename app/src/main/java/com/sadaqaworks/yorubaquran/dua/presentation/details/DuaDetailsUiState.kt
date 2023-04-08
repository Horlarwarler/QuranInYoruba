package com.sadaqaworks.yorubaquran.dua.presentation.details

import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel

data class DuaDetailsUiState(

    val duas: List<DuaItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val messages: List<String> = emptyList(),
    val title: String = "Dua Chapter"
)
