package com.sadaqaworks.yorubaquran.quran.domain.model


data class DownloadNotification(
    val title: String,
    val description:String,
    val priority:Int ,
    val icon:Int,
    val silent:Boolean = true,
    val progress:Int? = null
)
