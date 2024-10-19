package com.sadaqaworks.yorubaquran.shared

import android.content.SharedPreferences
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.AUTO_PLAY
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.AUTO_SCROLL
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.CONTINUE_READING
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.DOWNLOAD_BEFORE_PLAYING
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.FONT_SIZE
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.LAST_SURAH
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.LAST_UPDATE
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.OFFSET
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.OUTDATED
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.POSITION
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.RECITER_NAME
import com.sadaqaworks.yorubaquran.shared.QuranPreference.Companion.VERSION
import com.sadaqaworks.yorubaquran.util.Constant.SUDAIS

class QuranPreferenceImp(
    private val preference: SharedPreferences
) : QuranPreference {

    override fun getReciterName(): String {
        return preference.getString(RECITER_NAME, SUDAIS) ?: SUDAIS
    }

    override fun saveReciterName(name: String) {
        preference.edit().putString(RECITER_NAME, name).apply()
    }

    override fun getLastSurah(): Int {
        return preference.getInt(LAST_SURAH, -1)
    }

    override fun saveLastSurah(id: Int) {
        preference.edit().putInt(LAST_SURAH, id).apply()

    }

    override fun downloadBeforePlaying(): Boolean {
        return preference.getBoolean(DOWNLOAD_BEFORE_PLAYING, true)
    }

    override fun saveDownloadBeforePlaying(save: Boolean) {
        preference.edit().putBoolean(DOWNLOAD_BEFORE_PLAYING, save).apply()
    }

    override fun getFontSize(): Int {
        return preference.getInt(FONT_SIZE, 20)
    }

    override fun saveFontSize(size: Int) {
        preference.edit().putInt(FONT_SIZE, size).apply()
    }

    override fun autoPlay(): Boolean {
        return preference.getBoolean(AUTO_PLAY, true)

    }

    override fun saveAutoPlay(autoPlay: Boolean) {
        preference.edit().putBoolean(AUTO_PLAY, autoPlay).apply()

    }

    override fun continueReading(): Boolean {
        return preference.getBoolean(CONTINUE_READING, true)
    }

    override fun saveContinueReading(continueReading: Boolean) {
        preference.edit().putBoolean(CONTINUE_READING, continueReading).apply()

    }

    override fun autoScroll(): Boolean {
        return preference.getBoolean(AUTO_SCROLL, true)

    }

    override fun saveAutoScroll(autoScroll: Boolean) {
        preference.edit().putBoolean(AUTO_SCROLL, autoScroll).apply()

    }

    override fun getOffset(): Int {
        return preference.getInt(OFFSET, 0)
    }

    override fun saveOffset(offset: Int) {
        preference.edit().putInt(OFFSET, offset).apply()
    }

    override fun getPosition(): Int {
        return preference.getInt(POSITION, 0)

    }

    override fun savePosition(position: Int) {
        preference.edit().putInt(POSITION, position).apply()
    }

    override fun isOutdated(): Boolean {
        return preference.getBoolean(OUTDATED, false)
    }

    override fun saveIsOutdated(isOutdated: Boolean) {
        preference.edit().putBoolean(OUTDATED, isOutdated).apply()
    }

    override fun getVersion(): Float {
       return preference.getFloat(VERSION, 1.0F)
    }

    override fun saveVersion(version: Float) {
        preference.edit().putFloat(VERSION, version).apply()
    }

    override fun lastUpdateTime(): String? {
        return preference.getString(LAST_UPDATE, null)
    }

    override fun editLastUpdateTime(time: String) {
       preference.edit().putString(LAST_UPDATE, time).apply()
    }



}