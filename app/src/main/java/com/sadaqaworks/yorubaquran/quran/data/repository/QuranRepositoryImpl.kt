package com.sadaqaworks.yorubaquran.quran.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sadaqaworks.yorubaquran.quran.data.database.BookmarkEntity

import com.sadaqaworks.yorubaquran.shared.QuranDatabase
import com.sadaqaworks.yorubaquran.quran.data.database.VerseEntity
import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToEntity
import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToModel
import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.quran.data.remote.RemoteQuran
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.VerseDto
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.VersionDto
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val quranDatabase: QuranDatabase,
    private val remoteQuran: RemoteQuran,
    private val quranPreference: QuranPreference
) : quranRepositoryInterface {
    val database = FirebaseFirestore.getInstance()

    override suspend fun getAllSurah(): List<SurahDetails> {
        return try {
           quranDatabase.quranDao().getAllSurah().map {
                it.convertToModel()
            }

        } catch (error: Exception) {
            Log.e("SURAH", "ERROR ${error.message}")
            emptyList()
        }
    }

    override suspend fun getAllAyah(surahId: Int): List<Verse> {
        return quranDatabase.quranDao().getAllAyah(surahId = surahId).map {
            it.convertToModel()
        }
    }


//    override suspend fun updateQuran(): Flow<Resource<Boolean>> {
//
//     return flow {
//            emit(Resource.Loading(true))
//            var errorMessage: String? = null
//            var verses: List<VerseDto> = emptyList()
//            try {
//                database
//                    .collection("verse")
//                    .get()
//                    .addOnSuccessListener { snapshot ->
//                        verses = snapshot.documents
//                            .mapNotNull {
//                                it.toObject(VerseDto::class.java)
//                            }
//                    }
//                    .addOnFailureListener {
//                        errorMessage = it.message ?: "Unknown Error"
//                    }
//                    .await()
//            } catch (error: FirebaseFirestoreException) {
//
//                errorMessage = "TRY AGAIN"
//            }
//
//            if (verses.isEmpty()) {
//                emit(Resource.Loading(false))
//                Log.d("TAG", "VERSE IS EMPTY")
//
//                emit(Resource.Error(errorMessage!!))
//                return@flow
//            }
//            val updatedAyahs = verses.map { verse ->
//                verse.convertToModel().convertToEntity()
//            }
//            insertAllAyah(updatedAyahs)
//          //  Log.d("TAG", "SUCCESSFULLY UPDATE ${updatedAyahs}")
//
//            emit(Resource.Loading(false))
//            emit(Resource.Success(true))
//            quranPreference.saveIsOutdated(false)
//
//        }
//
//    }



    private suspend fun insertAllAyah(updatedAyahs: List<VerseEntity>) {
        quranDatabase.quranDao().insertAyahEntity(updatedAyahs)
    }

    override suspend fun getAllBookmark(): Flow<Resource<List<Verse>>> {
        return flow {
            emit(Resource.Loading())
            val verses = mutableListOf<Verse>()
            try {
                val bookmarks = quranDatabase.quranDao().getAllBookmark()
                bookmarks.forEach {
                    val verseId = it.verseId
                    val surahId = it.surahId
                    val verse = quranDatabase.quranDao().getAyah(
                        verseId = verseId,
                        surahId = surahId
                    ).convertToModel()
                    verses.add(verse)

                }

                emit(Resource.Success(data = verses))
            } catch (e: Exception) {
                emit(Resource.Error(errorMessage = e.message ?: "Unknown Error"))
            }


        }


    }

    override suspend fun insertBookmark(verse: Verse) {
        val bookmarkEntity = BookmarkEntity(
            id =verse.id ,
            surahId =verse.surahId,
            verseId =verse.verseId
        )
        quranDatabase.quranDao().insertBookmark(bookmarkEntity)
    }

    override suspend fun getBookmarksVerseBySurah(surahId: Int): Flow<Resource<List<Int>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val verseIds = quranDatabase.quranDao().getBookmarksBySurah(surahId)
                    .map {
                        it.verseId
                    }

                emit(Resource.Success(data = verseIds))
            } catch (e: Exception) {
                emit(Resource.Error(errorMessage = e.message ?: "Unknown Error"))
            }

        }
    }

    override suspend fun deleteBookmark(id: Int) {
        quranDatabase.quranDao().deleteBookmark(
          id = id
        )
    }

    override suspend fun deleteAllBookmark() {
        quranDatabase.quranDao().deleteAllBookmark()
    }

    override suspend fun getAyah(ayahId: Int): Flow<Resource<Verse>> {
        return flow {
            emit(Resource.Loading())
            try {
                val ayah = quranDatabase.quranDao().getAyah(ayahId)
                val ayahModel = ayah.convertToModel()
                emit(Resource.Success(data = ayahModel))
            } catch (e: Exception) {
                emit(Resource.Error(errorMessage = e.message ?: "Unknown Error"))
            }
        }
    }


    override suspend fun getSurah(surahId: Int): Flow<Resource<SurahDetails>> {
        return flow {
            emit(Resource.Loading())
            try {
                val surahEntity = quranDatabase.quranDao().getSurah(surahId)
                val surahModel = surahEntity.convertToModel()
                emit(Resource.Success(data = surahModel))
            } catch (e: Exception) {
                emit(Resource.Error(errorMessage = e.message ?: "Unknown Error"))
            }
        }
    }

    override suspend fun getAyahAudio(id: Int, reciter: String): Flow<Resource<VerseAudioData>> {

        return flow {
            emit(Resource.Loading())
            val resource = remoteQuran.getQuranAudio(verse = id.toString(), reciter)
            emit(resource)

        }
    }

    override suspend fun getSurahAudio(
        surahId: Int,
        reciter: String
    ): Flow<Resource<SurahAudioData>> {
        return flow {
            emit(Resource.Loading())
            val resource =
                remoteQuran.getSurahAudio(surahId = surahId.toString(), reciterIdentifier = reciter)
            emit(resource)
        }

    }
}