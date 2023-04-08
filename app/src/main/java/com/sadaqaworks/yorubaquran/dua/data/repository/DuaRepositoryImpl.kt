package com.sadaqaworks.yorubaquran.dua.data.repository

import com.sadaqaworks.yorubaquran.dua.data.mapper.convertToModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.shared.QuranDatabase
import com.sadaqaworks.yorubaquran.util.Resource
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
                val dataBaseEntity = database.duaDao.getAllChapters(categoryId)
                val convertToModel = dataBaseEntity.map {
                    it.convertToModel()
                }
                emit(Resource.Success(convertToModel))
            }
            catch (error:Exception){
                emit(Resource.Error(error.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun getDuaByChapter(chapterId: Int): Flow<Resource<List<DuaItemModel>>> {
        return  flow {
            emit(Resource.Loading())
            try {
                val dataBaseEntity = database.duaDao.getAllDua(chapterId)
                val convertToModel = dataBaseEntity.map {
                    it.convertToModel()
                }
                emit(Resource.Success(convertToModel))
            }
            catch (error:Exception){
                emit(Resource.Error(error.message?:"Unknown Error"))
            }
        }
    }
}