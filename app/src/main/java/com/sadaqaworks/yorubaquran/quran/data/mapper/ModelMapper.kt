package com.sadaqaworks.yorubaquran.quran.data.mapper

import com.sadaqaworks.yorubaquran.quran.domain.model.Verse
import com.sadaqaworks.yorubaquran.quran.domain.model.Bookmark

fun Verse.convertToBookmark():Bookmark{
    return Bookmark(
        id = id,
        surahId = surahId,
        verseId = verseId,
        arabic = arabic,
        translation = translation,
        footnote = footnote
    )
}fun Bookmark.convertToAyahModel():Verse{
    return Verse(
        id = id,
        surahId = surahId,
        verseId = verseId,
        arabic = arabic,
        translation = translation,
        footnote = footnote
    )
}

