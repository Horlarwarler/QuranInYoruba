package com.sadaqaworks.yorubaquran.quran.presentation.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.util.ListHelper
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookmarkViewmodel @Inject constructor(
    private val  quranRepositoryInterface: quranRepositoryInterface
) : ViewModel() {
    private  val _bookmarkState  = MutableLiveData<BookmarkUiState>().apply {
        value = BookmarkUiState()
    }
    val stringListHelper = ListHelper<String>()

    val bookmarkState : LiveData<BookmarkUiState>
        get() = _bookmarkState
    init {
          getAllBookmark()
    }


    private fun getAllBookmark(){

        viewModelScope.launch {
            quranRepositoryInterface.getAllBookmark().collect{
                resource ->
                when(resource){
                    is Resource.Loading ->{

                    }
                    is Resource.Success -> {
                            _bookmarkState.value = bookmarkState.value?.copy(bookmarks = resource.data!!)
                    }
                    is Resource.Error ->{
                        val messages = bookmarkState.value?.messages!!

                        val newMessages = stringListHelper.addElement("${resource.errorMessage} occurs", messages)

                        _bookmarkState.value  = bookmarkState.value?.copy(messages = newMessages)
                    }
                }
            }
        }
    }

    fun deleteFromBookmark(ayahId:Int){
        viewModelScope.launch {
            quranRepositoryInterface.deleteBookmark(ayahId)
           getAllBookmark()
        }
        val messages = bookmarkState.value?.messages!!

        val newMessages = stringListHelper.addElement("Successfully Deleted ", messages)

        _bookmarkState.value  = bookmarkState.value?.copy(messages = newMessages)
    }

    fun deleteAllBookmark(){
        viewModelScope.launch {
            quranRepositoryInterface.deleteAllBookmark()
            getAllBookmark()
        }
        val messages = bookmarkState.value?.messages!!

        val newMessages = stringListHelper.addElement("Successfully Delete All ", messages)

        _bookmarkState.value  = bookmarkState.value?.copy(messages = newMessages)
    }
    fun deleteShowedMessage(){
        val messages = bookmarkState.value?.messages!!
        val newMessages = stringListHelper.removeElement( messages)
        _bookmarkState.value  = bookmarkState.value?.copy(messages = newMessages)
    }
}