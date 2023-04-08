package com.sadaqaworks.yorubaquran.settings.presentation.setting

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sadaqaworks.yorubaquran.util.ListHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val sharedPreference: SharedPreferences
): ViewModel() {
   private val continueReading = sharedPreference.getBoolean("continueReading", false)
    private val showNotification = sharedPreference.getBoolean("showNotification", false)
    private val downloadBeforePlaying = sharedPreference.getBoolean("downloadBeforePlaying",false)
    private val scrollAuto = sharedPreference.getBoolean("autoScroll",true)
    private val playAuto = sharedPreference.getBoolean("autoPlay",true)
    private val fontSize = sharedPreference.getInt("fontSize", 20)
    private val reciter = sharedPreference.getString("reciter","Abdurrahmaan As-Sudais")!!
    val stringListHelper = ListHelper<String>()

    private val _settingUiState  =  MutableLiveData<SettingScreenState> ().apply {
        value = SettingScreenState(
            continueReading = continueReading,
            downloadBeforePlaying = downloadBeforePlaying,
            showNotification = showNotification,
            fontSize = fontSize,
            reciter = reciter,
            scrollAuto = scrollAuto,
            playAuto = playAuto
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
                playAuto()
            }
        }
    }


    private fun continueReading(){
        val continueReading = _settingUiState.value?.continueReading!!
        val newValue = !continueReading
        sharedPreference.edit().putBoolean("continueReading", newValue).apply()
        //_settingUiState.value = settingUiState.value?.copy(continueReading = newValue)
    }
    private fun scrollAuto(){
        val scrollAuto = _settingUiState.value?.scrollAuto!!
        val newValue = !scrollAuto
        sharedPreference.edit().putBoolean("autoScroll", newValue).apply()
    }
    private fun playAuto(){
        val playAuto = _settingUiState.value?.playAuto!!
        val newValue = !playAuto
        sharedPreference.edit().putBoolean("autoPlay", newValue).apply()
    }

    private fun showNotification(){
        val showNotification = _settingUiState.value?.showNotification!!
        val newValue = !showNotification
        sharedPreference.edit().putBoolean("showNotification", newValue).apply()

        //_settingUiState.value = settingUiState.value?.copy(showNotification = newValue)
    }

    private fun downloadBeforePlaying(){
        val downloadBeforePlaying = _settingUiState.value?.downloadBeforePlaying!!
        val newValue = !downloadBeforePlaying
        sharedPreference.edit().putBoolean("downloadBeforePlaying", newValue).apply()

       // _settingUiState.value = settingUiState.value?.copy(downloadBeforePlaying = newValue)
    }

    private fun changeFontSize(fontSize:Int){
        sharedPreference.edit().putInt("fontSize", fontSize).apply()
     //   _settingUiState.value = settingUiState.value?.copy(fontSize = fontSize)
    }

    private fun changeReciter(reciter:String){
        sharedPreference.edit().putString("reciter",reciter).apply()
       // _settingUiState.value = settingUiState.value?.copy(reciter = reciter)
    }

    fun deleteShowedMessage(){
        val messages = settingUiState.value?.messages!!
        val newMessages = stringListHelper.removeElement( messages)
        _settingUiState.value  = settingUiState.value?.copy(messages = newMessages)
    }



}