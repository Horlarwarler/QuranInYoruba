package com.sadaqaworks.yorubaquran.qiblah.presentation

data class QiblahState(
    val location:String = "",
    val isAligned:Boolean= false,
    val errorMessages:List<String> = emptyList(),
    val requestPermission:Boolean = false
)
