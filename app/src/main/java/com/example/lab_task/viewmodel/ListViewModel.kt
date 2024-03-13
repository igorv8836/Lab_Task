package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.view.fragments.FiltersData
import kotlinx.coroutines.launch

class ListViewModel: ViewModel() {
    private var filters: FiltersData? = null
    private lateinit var loadedTags: List<TagEntity>
    val repository: TagRepository = TagRepository
    val tagsForDisplay: MutableLiveData<List<TagEntity>> = MutableLiveData()
    val helpingText = MutableLiveData<String>()
    val photoPathForOpenTag = MutableLiveData<String>()
    val currUsername = MutableLiveData<String>()

    init {
        loadTags()
        getErrorMessage()
    }

    fun loadTags(){
        viewModelScope.launch {
            repository.getTags().collect{
                loadedTags = it
                getCurrUserId()
                updateFilteredList()
            }
        }
    }

    fun updateFilteredList() {
        var temp = loadedTags
        filters?.let { currentFilters ->
            if (currentFilters.onlyWithPhoto) {
                temp = temp.filter { it.imagePath != null }
            }
            temp = when (currentFilters.sortType) {
                0 -> temp.sortedBy { it.likes }.reversed()
                1 -> temp.sortedBy { it.likes }
                2 -> temp.sortedBy { it.username }
                3 -> temp.sortedBy { it.username }.reversed()
                else -> temp
            }
            tagsForDisplay.value = temp
        } ?: run {
            tagsForDisplay.value = loadedTags
        }
    }


    fun applyFilters(data: FiltersData?){
        filters = data
        updateFilteredList()
    }

    fun getCurrUserId(){
        viewModelScope.launch {
            repository.getCurrUser().collect{
                currUsername.postValue(it.username)
            }
        }

    }

    fun getErrorMessage(){
        viewModelScope.launch {
            repository.errorMessage.collect{
                if (it != null)
                    helpingText.postValue(it)
            }
        }
    }

    fun changeLike(id: String, isLiked: Boolean){
        viewModelScope.launch {
            if (!isLiked) {
                repository.addLike(id)
            }else {
                repository.deleteLike(id)
            }
        }
    }

    fun deleteTag(tagId: String){
        viewModelScope.launch {
            repository.deleteTag(tagId)
        }
    }

}