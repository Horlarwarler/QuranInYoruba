package com.sadaqaworks.yorubaquran.dua.presentation.details

import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.dua.domain.model.RuqyahModel
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        Log.d("Chapter", "Chapter Id is $chapterId chapter name $chapterName")
        if (chapterId == 0) {
            getRuqyah()
        } else {
            getDuas(chapterId!!)

        }
    }

    private fun getDuas(chapterId: Int) {
        viewModelScope.launch {

            duaRepository.getDuaByChapter(chapterId).collectLatest { resource ->

                when (resource) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        _duaDetailsUiState.value =
                            duaDetailsUiState.value?.copy(duas = resource.data!!)
                    }

                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    private fun getRuqyah() {
        viewModelScope.launch(Dispatchers.IO) {

            duaRepository.getAllRuqyah().collectLatest { resource ->

                withContext(Dispatchers.Main){
                    when (resource) {
                        is Resource.Loading -> {
                            _duaDetailsUiState.value =
                                duaDetailsUiState.value?.copy(
                                    isLoading = resource.isLoading
                                )
                        }

                        is Resource.Success -> {
                            withContext(Dispatchers.Main) {
                                _duaDetailsUiState.value =
                                    duaDetailsUiState.value?.copy(
                                        duas = resource.data!!.map {
                                            it.mapToDuaItemModel()
                                        },
                                        isLoading = false
                                    )
                            }

                        }

                        is Resource.Error -> {
                            println("RUQYAH ERROR ${resource.errorMessage}")
                        }
                    }
                }

            }
        }
    }

}

private fun RuqyahModel.mapToDuaItemModel(): DuaItemModel {

    return DuaItemModel(
        duaId = id,
        chapterId = 0,
        duaArabic = arabic,
        duaTranslation = translation,
        duaReference = note ?: ""
    )
}