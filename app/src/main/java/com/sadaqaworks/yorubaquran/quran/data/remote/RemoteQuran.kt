package com.sadaqaworks.yorubaquran.quran.data.remote

import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.util.Resource

interface RemoteQuran {
    suspend fun getRemoteSurah():Resource<List<SurahDetails>>
    suspend fun getRemoteAllAyah():Resource<List<Verse>>
    suspend fun getRemoteVersion():Resource<Double>
    suspend fun getQuranAudio(verse:String, reciterIdentifier:String): Resource<VerseAudioData>
    suspend fun getSurahAudio(surahId:String, reciterIdentifier: String):Resource<SurahAudioData>

    companion object {
        const val versionUrl  =  "https://quranserver-production.up.railway.app/version"
        const val surahUrl =   "https://quranserver-production.up.railway.app/surah"
        const val ayahUrl = "https://quranserver-production.up.railway.app/quran"
        const val  ayahAudioUrl = "https://api.alquran.cloud/v1/ayah"
        const val  surahAudioUrl = "http://api.alquran.cloud/v1/surah"
    }
}