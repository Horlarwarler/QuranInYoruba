package com.sadaqaworks.yorubaquran.dua.presentation.chapter

import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.dua.domain.repository.DuaRepositoryInterface
import com.sadaqaworks.yorubaquran.dua.presentation.category.DuaCategories.DuaChapter.duaCategory
import com.sadaqaworks.yorubaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val duaRepository: DuaRepositoryInterface,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId = savedStateHandle.get<Int>("categoryId")
    private val _duaChapterUiState = MutableLiveData<DuaChaptersUiState>().apply {
        value = DuaChaptersUiState()
    }

    val duaChapterUiState: LiveData<DuaChaptersUiState>
        get() = _duaChapterUiState
    init {
        getChapters(categoryId!!)
        setName(categoryId)
    }



    private fun getChapters(categoryId:Int){
        viewModelScope.launch {
            duaRepository.getDuaChapters(categoryId).collectLatest {
                resource ->

                when (resource) {
                    is Resource.Loading ->{

                    }

                    is Resource.Success ->{
                       _duaChapterUiState.value =  duaChapterUiState.value?.copy(chapters = resource.data!!)
                    }

                    is Resource.Error ->{

                    }
                }
            }
        }
    }

    private fun setName(categoryId: Int){
        val duaCategory = duaCategory.find { it.categoryId == categoryId }
        _duaChapterUiState.value = duaChapterUiState.value?.copy(title = duaCategory!!.categoryName)
    }
}