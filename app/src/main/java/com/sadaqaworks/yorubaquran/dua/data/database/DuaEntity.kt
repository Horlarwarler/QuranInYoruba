package com.sadaqaworks.yorubaquran.dua.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("dua_category")
data class DuaCategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "category_name")
    val categoryName:String,

)


@Entity("dua_chapter")
data class DuaChapterEntity(
    @ColumnInfo(name = "chapter_id")
    @PrimaryKey
    val chapterId:Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "chapter_name")
    val chapterName:String,

)

@Entity("dua_item")
data class DuaItemEntity(
    @ColumnInfo(name = "dua_id")
    @PrimaryKey
    val duaId:Int,
    @ColumnInfo(name = "chapter_id")
    val chapterId: Int,
    @ColumnInfo(name = "dua_arabic")
    val duaArabic:String,
    @ColumnInfo(name = "dua_translation")
    val duaTranslation:String,
    @ColumnInfo(name = "dua_reference")
    val duaReference:String,

    )