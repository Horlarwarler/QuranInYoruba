package com.sadaqaworks.yorubaquran.quran.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verse_table")
data class VerseEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("surah_id")
    val surahId: Int,
    @ColumnInfo("verse_id")
    val verseId:Int,
    @ColumnInfo("verse_arabic")
    val arabic:String,
    @ColumnInfo("verse_translation")
    val translation:String,
    @ColumnInfo("verse_footnote")
    val footnote:String?
)
@Entity(tableName = "surah_table")
data class  SurahEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int ,
    @ColumnInfo("surah_id")
    val surahId: Int,
    @ColumnInfo("surah_type")
    val type:String,
    @ColumnInfo("ayah_number")
    val ayahNumber:Int,
    @ColumnInfo("surah_arabic")
    val arabic:String,
    @ColumnInfo("surah_translation")
    val translation: String
)
@Entity(tableName = "bookmark_table")
data class BookmarkEntity(
    @PrimaryKey
    val id: Int = 0,
    @ColumnInfo("surah_id")
    val surahId: Int,
    @ColumnInfo("verse_id")
    val verseId:Int,

)