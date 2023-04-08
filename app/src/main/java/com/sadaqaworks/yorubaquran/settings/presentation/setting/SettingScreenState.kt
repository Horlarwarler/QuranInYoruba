package com.sadaqaworks.yorubaquran.settings.presentation.setting

data class SettingScreenState(
    val continueReading: Boolean ,
    val downloadBeforePlaying:Boolean,
    val showNotification: Boolean,
    val fontSize:Int ,
    val reciter:String,
    val messages:List<String> = emptyList(),
    val scrollAuto:Boolean,
    val playAuto:Boolean
)
