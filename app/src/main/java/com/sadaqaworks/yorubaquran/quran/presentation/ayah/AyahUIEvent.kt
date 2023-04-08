package com.sadaqaworks.yorubaquran.quran.presentation.ayah

import com.sadaqaworks.yorubaquran.quran.domain.model.Verse

sealed class AyahUIEvent{

    class ShareButtonClick(val verse: Verse)  : AyahUIEvent()
    class BookmarkButtonClick(val verse: Verse): AyahUIEvent()
    class NavigatedFromScreen(val position: Int): AyahUIEvent()
    object PlayAyah:AyahUIEvent()
    object DownloadClick:AyahUIEvent()
    object PlayFromInternet:AyahUIEvent()
    class  OnSearchChange(val searchQuery:String):AyahUIEvent()
    class  SearchClick(val searchQuery: String):AyahUIEvent()
    object SwitchReciter:AyahUIEvent()
    class DownloadComplete(val isSuccessfully:Boolean =true):AyahUIEvent()
}
