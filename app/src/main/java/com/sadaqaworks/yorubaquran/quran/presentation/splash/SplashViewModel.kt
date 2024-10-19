package com.sadaqaworks.yorubaquran.quran.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.util.ListHelper
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val quranPreference: QuranPreference,
    private val quranRepositoryInterface: quranRepositoryInterface
) : ViewModel() {


    private val localVersion = quranPreference.getVersion()

    private var newVersion = localVersion

    val isOutDated = quranPreference.isOutdated()

    private val stringListHelper = ListHelper<String>()

    private val _splashUiState = MutableLiveData<SplashUiState>().apply {
        value = SplashUiState(
            isOutDated = isOutDated,
        )
    }
    val splashUiState: LiveData<SplashUiState>
        get() = _splashUiState


//    fun getQuranVersion() {
//
//        if (isOutDated) {
//            _splashUiState.value = splashUiState.value?.copy(isOutDated = true, navigateToHome = false)
//            return
//        }
//        if (!shouldCheckUpdate()) {
//            _splashUiState.value = splashUiState.value?.copy( navigateToHome = true)
//            return
//        }
//        _splashUiState.value = splashUiState.value?.copy(navigateToHome = true)
//        viewModelScope.launch {
//            _splashUiState.value = splashUiState.value?.copy(isLoading = true)
//            quranRepositoryInterface.getVersion().collectLatest { value ->
//                when (value) {
//                    is Resource.Loading -> {
//                        Log.d("Log", "Is loading 1")
//
//                        _splashUiState.value =
//                            splashUiState.value?.copy(isLoading = value.isLoading)
//                    }
//                    is Resource.Success -> {
//                        val version = value.data!!
//                        newVersion = version.toFloat()
//                        val currentDate = LocalDateTime.now().toString()
//                        quranPreference.editLastUpdateTime(currentDate)
//                        if (newVersion > localVersion) {
//                            quranPreference.saveIsOutdated(true)
//                            quranPreference.saveVersion(newVersion)
//                            _splashUiState.value = splashUiState.value?.copy(isOutDated = true)
//                        }
//                        else {
//                            _splashUiState.value = splashUiState.value?.copy(navigateToHome = true)
//                        }
//                    }
//
//                    is Resource.Error -> {
//                        val errorMessage = value.errorMessage ?: "Unknown Error"
//                        addToMessages(errorMessage)
//                        _splashUiState.value = splashUiState.value?.copy( navigateToHome = true)
//
//                    }
//                }
//            }
//        }
//    }


    private fun shouldCheckUpdate(): Boolean {
      //  return false

        val lastCheckDateString = quranPreference.lastUpdateTime() ?: return true
        val currentDate = LocalDateTime.now()
        val oneMonthAgo = currentDate.minusMonths(1)
        val lastCheckDate = LocalDateTime.parse(lastCheckDateString)
        return lastCheckDate.isBefore(oneMonthAgo)
    }

//    fun downloadUpdatedQuran() {
//        viewModelScope.launch {
//            quranRepositoryInterface.updateQuran().collect { resource ->
//
//                when (resource) {
//                    is Resource.Error -> {
//
//                        addToMessages(message = resource.errorMessage ?: "Uknown error")
//                        _splashUiState.value = splashUiState.value?.copy(
//                            isLoading = false,
//                            showDialog = false,
//                            downloadFailed = true
//                        )
//
//                    }
//
//                    is Resource.Loading -> {
//                        _splashUiState.value = splashUiState.value?.copy(isLoading = true, showDialog = false)
//                    }
//
//                    is Resource.Success -> {
//                        quranPreference.saveIsOutdated(false)
//                        _splashUiState.value = splashUiState.value?.copy(
//                            isLoading = false,
//                            isOutDated = false,
//                            isCanceled = true,
//                            downloadFailed = false,
//                            navigateToHome = true
//                        )
//                        addToMessages("Successfully Updated")
//
//                    }
//                }
//            }
//        }
//    }

    fun cancelDialogDialog() {
        _splashUiState.value = splashUiState.value?.copy(showDialog = false, navigateToHome = true)
    }

    fun hideDialog(){
        _splashUiState.value = splashUiState.value?.copy(showDialog = false)

    }

    fun dialogIsShown() {
        _splashUiState.value = splashUiState.value?.copy(showDialog = true)
    }

    private fun addToMessages(message: String) {
        val messages = splashUiState.value?.messages!!

        val newMessages = stringListHelper.addElement(message, messages)

        _splashUiState.value = splashUiState.value?.copy(messages = newMessages)
    }

    fun removeFromMessages() {
        val messages = splashUiState.value?.messages!!

        val newMessages = stringListHelper.removeElement(messages)

        _splashUiState.value = splashUiState.value?.copy(messages = newMessages)
    }

}