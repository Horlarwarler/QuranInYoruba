package com.sadaqaworks.yorubaquran.quran.presentation.splash

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqaworks.yorubaquran.quran.data.remote.RemoteQuran
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.util.ListHelper
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val quranRepositoryInterface: quranRepositoryInterface
) : ViewModel(){
    private val localVersion = sharedPreferences.getFloat("version",1.0F)

    private var newVersion = localVersion

    private val isOutDated = sharedPreferences.getBoolean("outdated", false)

    private val stringListHelper = ListHelper<String>()

    private val mockInternetConnection = true

    private val _splashUiState = MutableLiveData<SplashUiState>().apply {
        value = SplashUiState(isOutDated = isOutDated)
    }
    val splashUiState: LiveData<SplashUiState>
        get() = _splashUiState


      fun getQuranVersion(){
        Log.d("Log","Is loading ")
        if (isOutDated){
            return
        }
        viewModelScope.launch {
            _splashUiState.value = splashUiState.value?.copy(isLoading = true)
            quranRepositoryInterface.getVersion().collectLatest {
                value ->
                when(value){
                    is Resource.Loading ->{
                        Log.d("Log","Is loading 1")

                        _splashUiState.value = splashUiState.value?.copy(isLoading = value.isLoading)
                    }

                    is Resource.Success -> {

                        val version = value.data!!
                        newVersion = version.toFloat()
                        if (newVersion > localVersion){
                            sharedPreferences.edit().apply {
                                putBoolean("outdated", true)
                                putFloat("version",newVersion)
                                apply()
                            }
                            _splashUiState.value = splashUiState.value?.copy( isOutDated = true)

                        }

                    }

                    is Resource.Error -> {
                        val errorMessage = value.errorMessage?:"Unknown Error"
                        addToMessages(errorMessage)
                    }
                }
            }
        }
    }

     fun downloadUpdatedQuran(){
       viewModelScope.launch {
           quranRepositoryInterface.updateQuran().collect {
               resource ->

               when(resource){
                   is Resource.Error -> {

                       addToMessages(message = resource.errorMessage?:"Uknown error")
                      _splashUiState.value = splashUiState.value?.copy(isLoading=false,  showDialog = false, downloadFailed = true)

                   }
                   is Resource.Loading ->{
                     _splashUiState.value = splashUiState.value?.copy(isLoading = true)
                   }
                   is Resource.Success ->{
                       sharedPreferences.edit().putBoolean("outdated", false).apply()
                       _splashUiState.value = splashUiState.value?.copy(isLoading = false, isOutDated = false, isCanceled = true, downloadFailed =false )
                       addToMessages("Successfully Updated")

                   }
               }
           }
       }
    }

     fun setShowDialog(){
        _splashUiState.value = splashUiState.value?.copy(showDialog = false)
    }

    fun dialogIsShown(){
        _splashUiState.value = splashUiState.value?.copy(showDialog = true)
    }
    private fun addToMessages(message:String){
        val messages = splashUiState.value?.messages!!

        val newMessages = stringListHelper.addElement(message, messages)

        _splashUiState.value  = splashUiState.value?.copy(messages = newMessages)
    }

    fun removeFromMessages(){
        val messages = splashUiState.value?.messages!!

        val newMessages = stringListHelper.removeElement( messages)

        _splashUiState.value  = splashUiState.value?.copy(messages = newMessages)
    }

}