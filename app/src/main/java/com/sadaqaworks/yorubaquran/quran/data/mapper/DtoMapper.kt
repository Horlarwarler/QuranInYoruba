package com.sadaqaworks.yorubaquran.quran.data.mapper

import com.sadaqaworks.yorubaquran.quran.data.remote.dto.*
import com.sadaqaworks.yorubaquran.quran.domain.model.*


fun SurahDetailsDto.convertToModel(): SurahDetails{

    return  SurahDetails(
        id = id,
        arabic = arabic,
        surahId = surahId,
        translation = translation,
        ayahNumber = ayahNumber,
        type = type

    )
}

fun VerseDto.convertToModel(): Verse{

    return  Verse(
        id = id,
        surahId = surahId,
        verseId = verseId,
        arabic = arabic,
        translation = translation,
        footnote = footnote

    )
}

fun VersionDto.convertToModel(): Version {

    return Version(
        version = version
    )
}

fun VerseAudioDataDto.convertToModel():VerseAudioData{
    return VerseAudioData(
        audio = audio,
        number = number,
        audioSecondary = audioSecondary,
        numberInSurah = numberInSurah
    )
}

fun SurahAudioDataDto.convertToModel(): SurahAudioData{
    return  SurahAudioData(
        audioVerses = verses.map {
            it.convertToModel()
        }
    )
}

fun SurahAudioDto.convertToModel(): SurahAudio {
    return  SurahAudio(
        code = code,
        status = status,
        data = data.convertToModel()
    )

}





fun VerseAudioDto.convertToModel():VerseAudio{
    return VerseAudio(
        code = code,
        status = status,
        dataModel = data.convertToModel()
    )
}



