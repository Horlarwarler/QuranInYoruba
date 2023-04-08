package com.sadaqaworks.yorubaquran.quran.data.database

import androidx.room.*


@Dao
interface QuranDao {

    // Get all surah query
    @Query("SELECT * FROM surah_table ORDER BY id ASC")
    suspend fun getAllSurah():List<SurahEntity>

    @Query("SELECT * FROM verse_table WHERE surah_id == :surahId  ")
    suspend fun getAllAyah(surahId:Int): List<VerseEntity>

    @Query("SELECT * FROM verse_table WHERE id == :ayahId")
    suspend fun getAyah(ayahId:Int) : VerseEntity

    @Query("DELETE  FROM surah_table")
    suspend fun deleteAllSurah()

    @Query("DELETE FROM verse_table")
    suspend fun deleteAllAyah()

    @Insert
    suspend fun insertSurahEntity(surah: List<SurahEntity>)

    @Insert
    suspend fun insertAyahEntity(ayahs:List<VerseEntity>)

    @Query("SELECT * FROM  bookmarkTable ORDER BY id")
    suspend fun  getAllBookmark() : List<BookmarkEntity>

    @Query("SELECT * FROM bookmarkTable WHERE id == :id")
    suspend fun getBookmark(id: Int): BookmarkEntity

    @Query("DELETE FROM bookmarkTable where id == :id")
    suspend fun deleteBookmark(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertBookmark(bookmarkEntity: BookmarkEntity)

    @Query("DELETE  FROM bookmarkTable")
    suspend fun deleteAllBookmark()

    @Query("SELECT * FROM surah_table WHERE id == :surahId")
    suspend fun getSurah(surahId: Int): SurahEntity


}