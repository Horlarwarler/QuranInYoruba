package com.sadaqaworks.yorubaquran.shared

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sadaqaworks.yorubaquran.internet.InternetConnectionMonitor
import com.sadaqaworks.yorubaquran.internet.InternetConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val internetConnectionMonitor: InternetConnectionMonitor
): ViewModel(){
    private val _sharedViewModelState = MutableLiveData<ShareViewModelState>().apply {
        value = ShareViewModelState()
    }

    val shareViewModelState : LiveData<ShareViewModelState>
     get() = _sharedViewModelState

    init {
        internetConnectionMonitor.networkAvailable.asLiveData(Dispatchers.IO).observeForever {
            _sharedViewModelState.value = shareViewModelState.value?.copy(internetConnectionState = it)
        }
    }



}

data class ShareViewModelState(
    val internetConnectionState: InternetConnectionState = InternetConnectionState.UNAVAILABLE
)