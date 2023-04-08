package com.sadaqaworks.yorubaquran.dua.domain.repository

import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.util.Resource
import kotlinx.coroutines.flow.Flow

interface DuaRepositoryInterface {

    suspend fun getDuaChapters(categoryId:Int): Flow<Resource<List<DuaChapterModel>>>
    suspend fun getDuaByChapter(chapterId:Int):Flow<Resource<List<DuaItemModel>>>
}