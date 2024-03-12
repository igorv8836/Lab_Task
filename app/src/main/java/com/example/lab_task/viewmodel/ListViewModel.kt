package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.TagEntity
import kotlinx.coroutines.launch
import java.io.File

class ListViewModel: ViewModel() {
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
                tagsForDisplay.postValue(it)
                getCurrUserId()
            }
        }
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