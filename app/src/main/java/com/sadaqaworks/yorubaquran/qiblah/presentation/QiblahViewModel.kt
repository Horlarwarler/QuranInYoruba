package com.sadaqaworks.yorubaquran.qiblah.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sadaqaworks.yorubaquran.qiblah.Location
import com.sadaqaworks.yorubaquran.util.ListHelper
import dagger.hilt.android.lifecycle.HiltViewModel


class QiblahViewModel(
) : ViewModel() {
    private val _qiblahState = MutableLiveData<QiblahState> ().apply {
        value = QiblahState()
    }

    val qiblahState :LiveData<QiblahState>
    get() = _qiblahState
    private val stringListHelper = ListHelper<String>()


    private fun addErrorMessage(message:String){
        val messages = _qiblahState.value?.errorMessages!!

        val newMessages = stringListHelper.addElement(message, messages)

        _qiblahState.value  = _qiblahState.value?.copy(errorMessages = newMessages)
    }

    private fun removeMessage(){
        val messages = _qiblahState.value?.errorMessages!!

        val newMessages = stringListHelper.removeElement(messages)

        _qiblahState.value  = _qiblahState.value?.copy(errorMessages = newMessages)

    }


}