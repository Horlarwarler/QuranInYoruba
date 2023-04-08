package com.sadaqaworks.yorubaquran.quran.presentation.surah

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.util.NormalizeSearch
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SurahViewModel @Inject constructor(
    private val quranRepositoryInterface: quranRepositoryInterface,
    val savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences


)  : ViewModel(){

     var lastReadSurah = sharedPreferences.getString("lastReadSurah", null)
     var lastReadVerse = sharedPreferences.getInt("lastReadVerse",0)


    init {
           setLastRead()
    }

    fun setLastRead (){
        lastReadSurah = sharedPreferences.getString("lastReadSurah", null)
        lastReadVerse = sharedPreferences.getInt("lastReadVerse",0)
    }


    private val _surahScreenState = MutableLiveData<SurahScreenState>().apply {
        Log.d("Last read", "Last read $lastReadSurah $lastReadVerse")
        value = SurahScreenState()
    }
    val  surahScreenState : LiveData<SurahScreenState>
        get() = _surahScreenState
    private val _surahs = MutableLiveData<List<SurahDetails>?>()
    val surahs : MutableLiveData<List<SurahDetails>?>
        get() = _surahs

    private  val normalizeSearch = NormalizeSearch()


    init {
        getAllSurah()
        testCall()
    }

    private fun testCall(){
        viewModelScope.launch {
          //  downloadInterface.downloadFile("https://cdn.islamic.network/quran/audio/128/ar.alafasy/8.mp3","quran.mp3")
            //downloadInterface.downloadFile("https://cdn.islamic.network/quran/audio/192/ar.abdullahbasfar/262.mp3","quran192")

        }
    }
    private fun  getAllSurah(){
        viewModelScope.launch {
            quranRepositoryInterface.getAllSurah().collect{
                result ->
                when(result) {
                    is Resource.Loading -> {
                        Log.d("Surah ", "Loading")

                        _surahScreenState.value = _surahScreenState.value?.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        val allSurah = result.data!!
                        _surahScreenState.value = _surahScreenState.value?.copy(isLoading = false, allSurah = allSurah)
                        _surahs.value = result.data
                    }

                    is Resource.Error ->{
                        Log.d("Surah ", "Error occurs ${result.errorMessage}")
                    }
                }
            }
        }
    }




}