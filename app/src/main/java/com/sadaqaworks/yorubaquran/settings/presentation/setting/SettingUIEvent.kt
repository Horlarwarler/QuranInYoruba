package com.sadaqaworks.yorubaquran.settings.presentation.setting

sealed class SettingUIEvent{
    object ContinueReading: SettingUIEvent()
    object DownloadSurahBeforePlaying: SettingUIEvent()
    object PlayAuto:SettingUIEvent()
    object ScrollAuto:SettingUIEvent()
    class SelectReciter(val reciter:String): SettingUIEvent()
    class SelectFont(val fontSize:Int):SettingUIEvent()


}
