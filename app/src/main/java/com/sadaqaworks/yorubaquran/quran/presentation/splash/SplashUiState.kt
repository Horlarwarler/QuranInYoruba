package com.sadaqaworks.yorubaquran.quran.presentation.splash

data class SplashUiState(
    val isOutDated: Boolean,
    val isLoading:Boolean = false,
    val messages:List<String> = emptyList(),
    val isCanceled:Boolean = false,
    val showDialog:Boolean = false,
    val downloadFailed:Boolean = false,
    val navigateToHome:Boolean = false
)
