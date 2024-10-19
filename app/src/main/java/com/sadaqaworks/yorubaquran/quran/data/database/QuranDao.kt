package com.sadaqaworks.yorubaquran.quran.data.database

import androidx.room.*


@Dao
interface QuranDao {

    // Get all surah_table query
    @Query("SELECT * FROM surah_table ORDER BY id ASC")
    suspend fun getAllSurah():List<SurahEntity>

    @Query("SELECT * FROM verse_table WHERE surah_id == :surahId  ")
    suspend fun getAllAyah(surahId:Int): List<VerseEntity>

    @Query("SELECT * FROM verse_table WHERE verse_id ==:verseId AND surah_id == :surahId")
    suspend fun getAyah(verseId: Int, surahId: Int) : VerseEntity


    @Query("SELECT * FROM verse_table WHERE id == :ayahId")
    suspend fun getAyah(ayahId:Int) : VerseEntity

    @Query("DELETE  FROM surah_table")
    suspend fun deleteAllSurah()

    @Query("DELETE FROM verse_table")
    suspend fun deleteAllAyah()

    @Insert
    suspend fun insertSurahEntity(surah: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAyahEntity(ayahs:List<VerseEntity>)

    @Query("SELECT * FROM  bookmark_table ORDER BY id")
    suspend fun  getAllBookmark() : List<BookmarkEntity>

    @Query("SELECT * FROM bookmark_table WHERE surah_id ==:surahId")
    suspend fun getBookmarksBySurah(surahId: Int): List<BookmarkEntity>


    @Query("SELECT * FROM bookmark_table WHERE verse_id == :verseId AND surah_id ==:surahId")
    suspend fun getBookmark(verseId:Int, surahId: Int): BookmarkEntity

    @Query("DELETE FROM bookmark_table WHERE id == :id")
    suspend fun deleteBookmark(id:Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  insertBookmark(bookmarkEntity: BookmarkEntity)

    @Query("DELETE  FROM bookmark_table")
    suspend fun deleteAllBookmark()

    @Query("SELECT * FROM surah_table WHERE id == :surahId")
    suspend fun getSurah(surahId: Int): SurahEntity


}