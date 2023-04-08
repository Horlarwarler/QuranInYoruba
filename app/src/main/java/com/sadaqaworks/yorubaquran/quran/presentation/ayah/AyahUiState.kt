package com.sadaqaworks.yorubaquran.quran.presentation.ayah

import com.sadaqaworks.yorubaquran.quran.domain.model.*

data class AyahUiState(
    val ayahs: List<Verse> = emptyList(),
    val bookmarkIds: List<Int> = emptyList(),
    val selectedAyah: Verse?  = null,
    val selectedSurah:SurahDetails?= null,
    val isLoading:Boolean = false,
    val error:List<String> = emptyList(),
    val ayahToPlay:VerseAudioData? = null,
    val isPlaying:Boolean = false,
    val surahAudio: List<VerseAudioData>?  = null,
    val downloadedAudios:List<DownloadVerse> = emptyList(),
    val currentAyahIdInQuran:Int? = null,
    val nextAyahIdInQuran:Int? = null,
    val currentAyahIdInSurah:Int? = null,
    val showDialog:Boolean = false,
    val isCancelled:Boolean = false,
    val isCompleted:Boolean = false,
    val messages: List<String> = emptyList(),
    val searchText:String = "",
    val normalizedSearchText:String = "",
    val lastReadVerse: Int,
    val notifyUserAboutReciterSelection: Boolean,
    val notifyUserToDownloadRemaining:Boolean = false,
    val playFromInternet:Boolean,
    val fontSize:Float,
    val canPlay:Boolean = false,
    val surahId:Int,
    val autoPlay:Boolean,
    val autoScroll:Boolean,
    val searchIndex: List<Int> = emptyList(),
    val searchScrollIndex:Int?= null


)

