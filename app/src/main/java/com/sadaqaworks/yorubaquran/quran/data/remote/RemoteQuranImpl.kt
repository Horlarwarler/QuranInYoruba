package com.sadaqaworks.yorubaquran.quran.data.remote

import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToModel
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.*
import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.util.Resource
import com.sadaqaworks.yorubaquran.util.makeNetworkRequest
import io.ktor.client.*

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteQuranImpl @Inject constructor(
    private val client: HttpClient
)  : RemoteQuran {
    override suspend fun getRemoteSurah(): Resource<List<SurahDetails>> {
        val url = RemoteQuran.surahUrl
        val response = makeNetworkRequest<List<SurahDetailsDto>>(url, client)
        val data = response.first
        val errorMessage = response.second
        if (data == null ){
            return  Resource.Error(errorMessage?:"UnknownError")
        }
        val surahDetails = data.map {
            it.convertToModel()
        }
        return  Resource.Success(surahDetails)
    }

    override suspend fun getRemoteAllAyah(): Resource<List<Verse>> {
        TODO()
//        val url = RemoteQuran.ayahUrl
//        val response = makeNetworkRequest<SurahDto>(url, client)
//        val data = response.first
//        val errorMessage = response.second
//        if (data == null ){
//           return Resource.Error(errorMessage = errorMessage?:"Unknown Error")
//
//        }
//        val verses =data.convertToModel().verses
//        return Resource.Success(verses)

    }

    override suspend fun getRemoteVersion(): Resource<Double> {
        val url = RemoteQuran.versionUrl
        val response = makeNetworkRequest<VersionDto>(url, client)
        val data = response.first
        val errorMessage = response.second
        if (data == null ){
            return Resource.Error(errorMessage = errorMessage?:"Unknown Error")

        }
        val version =data.convertToModel().version
        return Resource.Success(version)
    }

    override suspend fun getQuranAudio(
        verse: String,
        reciterIdentifier: String
    ): Resource<VerseAudioData> {
        val url  = "${RemoteQuran.ayahAudioUrl}/$verse/$reciterIdentifier"
        val response  = makeNetworkRequest<VerseAudioDto>(url, client)
        val data = response.first
        val errorMessage = response.second
        if (data == null ){
            return Resource.Error(errorMessage = errorMessage?:"Unknown Error")
        }
        val verseAudio =data.convertToModel().dataModel
        return  Resource.Success(verseAudio)

    }

    override suspend fun getSurahAudio(
        surahId: String,
        reciterIdentifier: String
    ): Resource<SurahAudioData> {
        val url  = "${RemoteQuran.surahAudioUrl}/$surahId/$reciterIdentifier"
        val response  = makeNetworkRequest<SurahAudioDto>(url, client)
        val data = response.first
        val errorMessage = response.second
        if (data == null ){
            return Resource.Error(errorMessage = errorMessage?:"Unknown Error")
        }
        val surahAudioData =data.convertToModel().data
        return Resource.Success(surahAudioData)

    }
}