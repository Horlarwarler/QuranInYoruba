package com.sadaqaworks.yorubaquran.quran.data.repository

import android.util.Log
import com.sadaqaworks.yorubaquran.shared.QuranDatabase
import com.sadaqaworks.yorubaquran.quran.data.database.VerseEntity
import com.sadaqaworks.yorubaquran.quran.data.database.SurahEntity
import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToEntity
import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToModel
import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.quran.data.remote.RemoteQuran
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QuranRepositoryImpl  @Inject constructor(
    private val quranDatabase: QuranDatabase,
    private  val remoteQuran: RemoteQuran
): quranRepositoryInterface{

    override suspend fun getAllSurah(): Flow<Resource<List<SurahDetails>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val offlineSurah = quranDatabase.quranDao.getAllSurah().map {
                    it.convertToModel()
                }
                emit(Resource.Success(offlineSurah))
            }
            catch (error:Exception){
                emit(Resource.Error(error.message?:"Unknown Error"))
            }

        }
    }

    override suspend fun getAllAyah(surahId:Int ): Flow<Resource<List<Verse>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val offlineQuran = quranDatabase.quranDao.getAllAyah(surahId = surahId).map {
                    it.convertToModel()
                }
                emit(Resource.Success(offlineQuran))
            }
            catch (error:Exception){
                emit(Resource.Error(error.message?:"Unknown Error"))
            }
        }
    }



    override suspend fun updateQuran(): Flow<Resource<Boolean>> {
        return  flow {
            emit(Resource.Loading(true))
            val result = remoteQuran.getRemoteAllAyah()
            if (result is Resource.Error){
                emit(Resource.Loading(false))
                emit(Resource.Error(result.errorMessage!!))
                return@flow
            }
            val data = result as Resource.Success
            clearAyahDatabase()
            val  updatedAyahs  = data.data!!.map {
                    verse ->
                verse.convertToEntity()
            }
            insertAllAyah(updatedAyahs)
            emit(Resource.Loading(false))
            emit(Resource.Success(true))

        }

    }

    override suspend fun getVersion(): Flow<Resource<Double>> {
       return flow {
           emit(Resource.Loading(isLoading = true))
           val version = remoteQuran.getRemoteVersion()
           when(version){
               is Resource.Loading ->{
                   //
               }
               is Resource.Success ->{
                   emit(Resource.Loading(isLoading = false))
                   emit(Resource.Success(version.data!!))
               }
               is Resource.Error ->{
                   emit(Resource.Loading(isLoading = false))
                   emit(Resource.Error(version.errorMessage!!))
               }
           }
       }
    }

    private  suspend fun  clearAyahDatabase(){
        quranDatabase.quranDao.deleteAllAyah()
    }

    private suspend fun insertAllAyah(updatedAyahs: List<VerseEntity>){
        quranDatabase.quranDao.insertAyahEntity(updatedAyahs)
    }

    private  suspend fun  clearSurahDatabase(){
        quranDatabase.quranDao.deleteAllSurah()
    }

    private suspend fun insertAllSurah(updatedSurah: List<SurahEntity>){
        quranDatabase.quranDao.insertSurahEntity(updatedSurah)
    }

    override suspend fun getAllBookmark(): Flow<Resource<List<Bookmark>>> {
        return  flow {
            emit(Resource.Loading())
            try {
                val bookmark =  quranDatabase.quranDao.getAllBookmark()
                val bookmarkModel = bookmark.map {
                    it.convertToModel()
                }
                emit(Resource.Success(data = bookmarkModel))
            }
            catch (e:Exception){
                emit(Resource.Error(errorMessage = e.message?:"Unknown Error"))
            }



        }


    }

    override suspend fun insertBookmark(bookmark: Bookmark) {
        val bookmarkEntity = bookmark.convertToEntity()
        quranDatabase.quranDao.insertBookmark(bookmarkEntity)
    }

    override suspend fun deleteBookmark(ayahId: Int) {
        quranDatabase.quranDao.deleteBookmark(ayahId)
    }

    override suspend fun deleteAllBookmark() {
       quranDatabase.quranDao.deleteAllBookmark()
    }

    override suspend fun getAyah(ayahId: Int): Flow<Resource<Verse>> {
        return flow {
            emit(Resource.Loading())
            try {
                val ayah = quranDatabase.quranDao.getAyah(ayahId)
                val ayahModel = ayah.convertToModel()
                emit(Resource.Success(data = ayahModel))
            }
            catch (e:Exception){
                emit(Resource.Error(errorMessage = e.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun getBookmark(ayahId: Int): Flow<Resource<Bookmark>> {
        return flow {
            emit(Resource.Loading())
            try {
                val bookmark = quranDatabase.quranDao.getBookmark(ayahId)
                val bookmarkModel = bookmark.convertToModel()
                emit(Resource.Success(data = bookmarkModel))
            }
            catch (e:Exception){
                emit(Resource.Error(errorMessage = e.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun getSurah(surahId: Int): Flow<Resource<SurahDetails>> {
        return flow {
            emit(Resource.Loading())
            try {
                val surahEntity = quranDatabase.quranDao.getSurah(surahId)
                val surahModel = surahEntity.convertToModel()
                emit(Resource.Success(data = surahModel))
            }
            catch (e:Exception){
                emit(Resource.Error(errorMessage = e.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun getAyahAudio(id: Int, reciter:String): Flow<Resource<VerseAudioData>> {

        return flow {
            emit(Resource.Loading())
            val resource = remoteQuran.getQuranAudio(verse = id.toString(), reciter )
            emit(resource)

        }
    }

    override suspend fun getSurahAudio(
        surahId: Int,
        reciter: String
    ): Flow<Resource<SurahAudioData>> {
        return  flow {
            emit(Resource.Loading())
            val resource = remoteQuran.getSurahAudio(surahId = surahId.toString(), reciterIdentifier = reciter)
            emit(resource)
        }

    }
}