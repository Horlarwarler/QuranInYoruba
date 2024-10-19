package com.sadaqaworks.yorubaquran.dua.data.repository

import com.sadaqaworks.yorubaquran.dua.data.mapper.convertToModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.dua.domain.model.RuqyahModel
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.quran.data.database.VerseEntity
import com.sadaqaworks.yorubaquran.shared.QuranDatabase
import com.sadaqaworks.yorubaquran.util.Resource
import com.sadaqaworks.yorubaquran.util.surahList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DuaRepositoryImpl @Inject constructor(
    private val database: QuranDatabase
) : DuaRepositoryInterface {

    override suspend fun getDuaChapters(categoryId: Int): Flow<Resource<List<DuaChapterModel>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val dataBaseEntity = database.duaDao().getAllChapters(categoryId)
                val convertToModel = dataBaseEntity.map {
                    it.convertToModel()
                }
                emit(Resource.Success(convertToModel))
            } catch (error: Exception) {
                emit(Resource.Error(error.message ?: "Unknown Error"))
            }
        }
    }

    override suspend fun getDuaByChapter(chapterId: Int): Flow<Resource<List<DuaItemModel>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val dataBaseEntity = database.duaDao().getAllDua(chapterId)
                val convertToModel = dataBaseEntity.map {
                    it.convertToModel()
                }
                emit(Resource.Success(convertToModel))
            } catch (error: Exception) {
                emit(Resource.Error(error.message ?: "Unknown Error"))
            }
        }
    }

    override suspend fun getAllRuqyah(): Flow<Resource<List<RuqyahModel>>> {
        return flow {
            emit(Resource.Loading(true))
            val tempRuqyahList = mutableListOf<RuqyahModel>()
            try {
                database.duaDao().getAllRuqyah()
                    .forEach { ruqyah ->
                        val surahId = ruqyah.surahId
                        val surahName = surahList[surahId - 1]
                        val startVerse = ruqyah.startVerse
                        val endVerse = ruqyah.endVerse ?: run {
                            val verse = getVerseBySurahAndVerse(
                                surah = surahId,
                                verse = startVerse
                            )
                            val translation = "$surahId - ${surahName.capitalize()}: ${verse.translation} <$startVerse>"
//                            val translation = buildString {
//
//                                append("$surahId -")
//                                append("$surahName: ")
//                                append(verse.translation)
//                                append("<$startVerse>")
////
//
//                            }
                            val arabic = "${verse.arabic} <$startVerse>"
                            val verseInQoute = "&#xFD3E"
//                            val arabic = buildString {
//                                append(verse.arabic)
//                                //&#xFD3E
//                                append("<;$startVerse>")
//                            }
                            val ruqyahModel = ruqyah.convertToModel(
                                arabic = arabic,
                                translation = translation
                            )
                            tempRuqyahList.add(ruqyahModel)
                            return@forEach
                        }
                        var translation = "$surahId-${surahName.capitalize()}: "
                        var arabic = ""
                        (startVerse..endVerse).forEach { verseId ->
                            val verse = getVerseBySurahAndVerse(
                                surah = surahId,
                                verse = verseId
                            )
                            translation = translation.plus(verse.translation)
                            translation = translation.plus(" <$verseId> ")
                            arabic = arabic.plus(verse.arabic)
                            arabic = arabic.plus("<$verseId>")

                        }


                        val ruqyahModel = ruqyah.convertToModel(
                            arabic = arabic,
                            translation = translation
                        )
                        tempRuqyahList.add(ruqyahModel)


                    }
                emit(Resource.Success(tempRuqyahList))


            } catch (error: Exception) {
                println("ERROR FROM REPOSITORY ${error.message} cause ${error.cause}")
                emit(Resource.Loading(false))
                emit(Resource.Error(error.message ?: "Unknown Error"))
            }
        }
    }

    private suspend fun getVerseBySurahAndVerse(
        surah: Int,
        verse: Int
    ): VerseEntity {


        return database.quranDao().getAyah(
            surahId = surah,
            verseId = verse
        )
    }
}