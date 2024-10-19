package com.sadaqaworks.yorubaquran.quran.presentation.surah

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.util.NormalizeSearch
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SurahViewModel @Inject constructor(
    private val quranRepositoryInterface: quranRepositoryInterface,
    val savedStateHandle: SavedStateHandle,
    private val quranPreference: QuranPreference


) : ViewModel() {

    var lastReadSurah = quranPreference.getLastSurah()
    var lastReadVerse = quranPreference.getPosition()





    private val _surahs = MutableLiveData<List<SurahDetails>?>()
    val surahs: MutableLiveData<List<SurahDetails>?>
        get() = _surahs


    init {
        getAllSurah()
        setLastRead()
    }



    private fun getAllSurah() {
        viewModelScope.launch {
           val surahs =  quranRepositoryInterface.getAllSurah()

            _surahs.value = surahs
        }
    }

    fun setLastRead() {
        lastReadSurah = quranPreference.getLastSurah()
        lastReadVerse = quranPreference.getPosition()
    }


}