package com.sadaqaworks.yorubaquran.util

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.sadaqaworks.yorubaquran.BuildConfig
import com.sadaqaworks.yorubaquran.quran.domain.model.VerseAudioData
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadVerse

const val numberKey = "numberInSurah"
const val surahKey = "surahId"

fun  convertToMediaFile(
    id: Int,
    url: String? = null,
    uri: Uri? = null,
    title:String,
    numberInSurah:Int,
    surahId:Int
): MediaBrowserCompat.MediaItem {
    val bundle = Bundle(
    )
    bundle.putInt(numberKey,numberInSurah)
    bundle.putInt(surahKey, surahId)
    val mediaDescription = MediaDescriptionCompat.Builder()
        .setMediaId(id.toString())
        .setTitle(title)
        .setMediaUri(
            uri ?: Uri.parse(url)
        )
        .setExtras(bundle)
        .build()
    return MediaBrowserCompat.MediaItem(
        mediaDescription,
        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )

}

fun List<VerseAudioData>.convertToMediaItem(): List<MediaBrowserCompat.MediaItem> {
    val mutableList: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
    this.forEach { ayah ->
        val media = convertToMediaFile(
            id = ayah.number,
            url = ayah.audio,
            title = ayah.surahName!!,
            surahId = ayah.surahId!!,
            numberInSurah = ayah.numberInSurah
        )
        mutableList.add(media)

    }

    return  mutableList.toList()
}

fun List<DownloadVerse>.convertToMedia(): List<MediaBrowserCompat.MediaItem> {
    val mutableList: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
    this.forEach { ayah ->
        val media = convertToMediaFile(
            id = ayah.id,
            uri = ayah.uri,
            title = ayah.surahName!!,
            surahId = ayah.surahId!!,
            numberInSurah = ayah.numberInSurah!!
        )
        mutableList.add(media)

    }
    val sortedList = mutableList
        .sortedBy {
            val id = it.mediaId?.toInt()!!
            id
        }
        .toList()
    return  sortedList

}

private fun getAlbumArtUri(): String? {
    return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            BuildConfig.APPLICATION_ID + "/drawable/mecca"
}

fun convertMediaItem(media:MediaBrowserCompat.MediaItem): MediaMetadataCompat {

    val numberInSurah = media.description.extras?.getInt(numberKey, 0)
    val surahId = media.description.extras?.getInt(surahKey,0)

    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, media.description.title.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getAlbumArtUri()!!)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "Description")
        .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR,"SUBTITLE")
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, media.description.mediaId)
        .putLong(numberKey, numberInSurah!!.toLong())
        .putLong(surahKey, surahId!!.toLong())
        .build()
}



