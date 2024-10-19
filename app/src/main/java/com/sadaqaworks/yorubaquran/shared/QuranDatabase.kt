package com.sadaqaworks.yorubaquran.shared

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sadaqaworks.yorubaquran.dua.data.database.DuaCategoryEntity
import com.sadaqaworks.yorubaquran.dua.data.database.DuaChapterEntity
import com.sadaqaworks.yorubaquran.dua.data.database.DuaDao
import com.sadaqaworks.yorubaquran.dua.data.database.DuaItemEntity
import com.sadaqaworks.yorubaquran.dua.data.database.RuqyahEntity
import com.sadaqaworks.yorubaquran.quran.data.database.VerseEntity
import com.sadaqaworks.yorubaquran.quran.data.database.BookmarkEntity
import com.sadaqaworks.yorubaquran.quran.data.database.QuranDao
import com.sadaqaworks.yorubaquran.quran.data.database.SurahEntity


@Database(entities = [
    VerseEntity::class,
    SurahEntity::class,
    BookmarkEntity::class,
    DuaItemEntity::class,
    DuaChapterEntity::class,
    DuaCategoryEntity::class,
    RuqyahEntity::class],
    version = 1, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {
    abstract  fun quranDao(): QuranDao
    abstract fun duaDao():DuaDao
}