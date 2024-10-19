package com.sadaqaworks.yorubaquran.quran.domain.repository

import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.util.Resource
import kotlinx.coroutines.flow.Flow

interface quranRepositoryInterface {
    suspend fun getAllSurah(): List<SurahDetails>
    suspend fun getAllAyah(surahId: Int): List<Verse>
   // suspend fun updateQuran(): Flow<Resource<Boolean>>
    suspend fun getAllBookmark(): Flow<Resource<List<Verse>>>
    suspend fun insertBookmark(
        verse: Verse
    )

    suspend fun deleteBookmark(
       id: Int
    )

    suspend fun deleteAllBookmark()
    suspend fun getAyah(ayahId: Int): Flow<Resource<Verse>>

    suspend fun getBookmarksVerseBySurah(surahId: Int): Flow<Resource<List<Int>>>
    suspend fun getSurah(surahId: Int): Flow<Resource<SurahDetails>>
    suspend fun getAyahAudio(id: Int, reciter: String): Flow<Resource<VerseAudioData>>
    suspend fun getSurahAudio(surahId: Int, reciter: String): Flow<Resource<SurahAudioData>>


}
