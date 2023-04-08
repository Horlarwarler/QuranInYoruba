package com.sadaqaworks.yorubaquran.quran.domain.repository

import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.util.Resource
import kotlinx.coroutines.flow.Flow

interface quranRepositoryInterface {
    suspend fun getAllSurah(): Flow<Resource<List<SurahDetails>>>
    suspend fun getAllAyah(surahId:Int):Flow<Resource<List<Verse>>>
    suspend fun updateQuran(): Flow<Resource<Boolean>>
    suspend fun getAllBookmark():Flow<Resource<List<Bookmark>>>
    suspend fun insertBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(ayahId:Int)
    suspend fun deleteAllBookmark()
    suspend fun getAyah(ayahId: Int):Flow<Resource<Verse>>
    suspend fun getBookmark(ayahId: Int):Flow<Resource<Bookmark>>
    suspend fun getSurah(surahId: Int):Flow<Resource<SurahDetails>>
    suspend fun getAyahAudio(id:Int, reciter:String):Flow<Resource<VerseAudioData>>
    suspend fun  getSurahAudio(surahId:Int,reciter: String): Flow<Resource<SurahAudioData>>
    suspend fun getVersion():Flow<Resource<Double>>


}
