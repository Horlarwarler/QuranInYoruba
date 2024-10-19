package com.sadaqaworks.yorubaquran.shared

interface QuranPreference {

    fun getReciterName ():String
    fun saveReciterName(name:String)
    fun getLastSurah ():Int
    fun saveLastSurah(id:Int)

    fun downloadBeforePlaying():Boolean

    fun saveDownloadBeforePlaying(save:Boolean)

    fun isOutdated():Boolean

    fun saveIsOutdated(isOutdated:Boolean)
    fun getFontSize():Int

    fun getVersion():Float

    fun saveVersion(version:Float)


    fun saveFontSize(size:Int)

    fun autoPlay():Boolean

    fun saveAutoPlay(autoPlay:Boolean)

    fun continueReading():Boolean

    fun saveContinueReading(continueReading:Boolean)


    fun autoScroll():Boolean

    fun saveAutoScroll(autoScroll:Boolean)

    fun getOffset():Int

    fun saveOffset(offset:Int)

    fun getPosition():Int

    fun savePosition(position:Int)

    fun lastUpdateTime():String?


    fun editLastUpdateTime(time:String)






    companion object {
        const val RECITER_NAME = "RECITER_NAME"
        const val DOWNLOAD_BEFORE_PLAYING = "DOWNLOAD_BEFORE_PLAYING"
        const val FONT_SIZE = "FONT_SIZE"
        const val AUTO_PLAY = "AUTO_PLAY"
        const val AUTO_SCROLL = "AUTO_SCROLL"
        const val OFFSET = "OFFSET"
        const val POSITION = "POSITION"
        const val LAST_SURAH ="LAST_SURAH"
        const val CONTINUE_READING = "CONTINUE_READING"
        const val VERSION = "VERSION"
        const val OUTDATED = "OUTDATED"
        const val LAST_UPDATE = "LAST_UPDATE"

    }
}