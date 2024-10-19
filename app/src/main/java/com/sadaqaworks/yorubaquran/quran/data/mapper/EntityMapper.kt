package com.sadaqaworks.yorubaquran.quran.data.mapper

import com.sadaqaworks.yorubaquran.quran.data.database.VerseEntity
import com.sadaqaworks.yorubaquran.quran.data.database.BookmarkEntity
import com.sadaqaworks.yorubaquran.quran.data.database.SurahEntity
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse
import com.sadaqaworks.yorubaquran.quran.domain.model.Bookmark
import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails

fun SurahEntity.convertToModel(): SurahDetails {
    return  SurahDetails(
        id = id,
        surahId = surahId,
        type = type,
        ayahNumber = ayahNumber,
        translation = translation,
        arabic = arabic,


        )
}


fun VerseEntity.convertToModel(): Verse {
    return  Verse(
        id = id,
        surahId = surahId,
        verseId = verseId,
        arabic = arabic,
        translation = translation,
        footnote = footnote
    )
}

fun Verse.convertToEntity(): VerseEntity {

    return  VerseEntity(
        id = id,
        surahId = surahId,
        verseId = verseId,
        arabic = arabic,
        translation = translation,
        footnote = footnote

    )
}


fun SurahDetails.convertToEntity(): SurahEntity {

    return  SurahEntity(
        id = id,
        surahId = surahId,
        arabic = arabic,
        translation = translation,
        ayahNumber = ayahNumber,
        type = type

    )
}

