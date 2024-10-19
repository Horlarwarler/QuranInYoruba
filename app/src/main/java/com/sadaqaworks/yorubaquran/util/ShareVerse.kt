package com.sadaqaworks.yorubaquran.util

import android.app.Activity
import android.content.Intent
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse

fun Activity.shareClick(ayahSelected: Verse) {
    val body = "*Quran ${ayahSelected.surahId} Verse ${ayahSelected.verseId}*\n" +
            "${ayahSelected.arabic}\n\n" +
            "${ayahSelected.translation}\n\n" +
            " _${ayahSelected.footnote}_"
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, body)
    startActivity(Intent.createChooser(intent, "Share Surah"))
}