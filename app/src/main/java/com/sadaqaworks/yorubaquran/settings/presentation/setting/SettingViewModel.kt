package com.sadaqaworks.yorubaquran.settings.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.util.ListHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val quranPreference: QuranPreference
): ViewModel() {
   private val continueReading = quranPreference.continueReading()
    private val downloadBeforePlaying = quranPreference.downloadBeforePlaying()
    private val scrollAuto = quranPreference.autoScroll()
    private val autoPlay = quranPreference.autoPlay()
    private val fontSize = quranPreference.getFontSize()
    private val reciter =quranPreference.getReciterName()
    val stringListHelper = ListHelper<String>()

    private val _settingUiState  =  MutableLiveData<SettingScreenState> ().apply {
        value = SettingScreenState(
            continueReading = continueReading,
            downloadBeforePlaying = downloadBeforePlaying,
            fontSize = fontSize,
            reciter = reciter,
            scrollAuto = scrollAuto,
            playAuto = autoPlay
        )
    }
    val settingUiState:LiveData<SettingScreenState>
        get() = _settingUiState
    init {

    }

    fun handleUiEvent(settingUIEvent: SettingUIEvent){

        when(settingUIEvent){
            is SettingUIEvent.ContinueReading ->{
                continueReading()
            }

            is SettingUIEvent.DownloadSurahBeforePlaying ->{
                downloadBeforePlaying()
            }


            is SettingUIEvent.SelectFont ->{
                changeFontSize(settingUIEvent.fontSize)
            }
            is SettingUIEvent.SelectReciter ->{
                changeReciter(settingUIEvent.reciter)
            }
            is SettingUIEvent.ScrollAuto -> {
                scrollAuto()
            }

            is SettingUIEvent.PlayAuto -> {
                autoPlay()
            }
        }
    }


    private fun continueReading(){
        val continueReading = _settingUiState.value?.continueReading!!
        val newValue = !continueReading
        quranPreference.saveContinueReading(newValue)
    }
    private fun scrollAuto(){
        val scrollAuto = _settingUiState.value?.scrollAuto!!
        val newValue = !scrollAuto
        quranPreference.saveAutoScroll(newValue)
    }
    private fun autoPlay(){
        val playAuto = _settingUiState.value?.playAuto!!
        val newValue = !playAuto
        quranPreference.saveAutoPlay(newValue)
    }


    private fun downloadBeforePlaying(){
        val downloadBeforePlaying = _settingUiState.value?.downloadBeforePlaying!!
        val newValue = !downloadBeforePlaying
        quranPreference.saveDownloadBeforePlaying(newValue)

       // _settingUiState.value = settingUiState.value?.copy(downloadBeforePlaying = newValue)
    }

    private fun changeFontSize(fontSize:Int){
        quranPreference.saveFontSize(fontSize)
     //   _settingUiState.value = settingUiState.value?.copy(fontSize = fontSize)
    }

    private fun changeReciter(reciter:String){
        quranPreference.saveReciterName(reciter)
       // _settingUiState.value = settingUiState.value?.copy(reciter = reciter)
    }



}