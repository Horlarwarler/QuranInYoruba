package com.sadaqaworks.yorubaquran.dua.data.database

import androidx.room.Dao
import androidx.room.Query


@Dao
interface DuaDao {

    @Query("SELECT * FROM dua_category")
    suspend fun getAllCategories(): List<DuaCategoryEntity>

    @Query("SELECT * FROM dua_chapter WHERE  category_id == :categoryId")

    suspend fun getAllChapters(categoryId:Int): List<DuaChapterEntity>

    @Query("SELECT * FROM dua_item WHERE chapter_id == :chapterId ")

    suspend fun getAllDua(chapterId:Int): List<DuaItemEntity>

    @Query("SELECT * FROM ruqyah ORDER by id")
    suspend fun getAllRuqyah():List<RuqyahEntity>
}