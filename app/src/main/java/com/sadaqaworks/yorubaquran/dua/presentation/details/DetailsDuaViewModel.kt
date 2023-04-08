package com.sadaqaworks.yorubaquran.dua.presentation.details

import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailsDuaViewModel @Inject constructor(
    private val duaRepository: DuaRepositoryInterface,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chapterId = savedStateHandle.get<Int>("chapterId")
    private val chapterName = savedStateHandle.get<String>("chapterName")

    private val _duaDetailsUiState = MutableLiveData<DuaDetailsUiState>().apply {
        value = DuaDetailsUiState(title = chapterName!!)
    }

    val duaDetailsUiState: LiveData<DuaDetailsUiState>
        get() = _duaDetailsUiState

    init {

      //  _duaDetailsUiState.value = duaDetailsUiState.value?.copy(title = chapterName!!)
        Log.d("Chapter", "Chapter Id is $chapterId")
        getDuas(chapterId!!)
    }

    private fun getDuas(chapterId:Int){
        viewModelScope.launch {
            duaRepository.getDuaByChapter(chapterId).collectLatest {
                    resource ->

                when (resource) {
                    is Resource.Loading ->{

                    }

                    is Resource.Success ->{
                        _duaDetailsUiState.value =  duaDetailsUiState.value?.copy(duas = resource.data!!)
                    }

                    is Resource.Error ->{

                    }
                }
            }
        }
    }

}