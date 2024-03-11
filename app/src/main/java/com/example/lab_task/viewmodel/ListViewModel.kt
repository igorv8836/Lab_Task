package com.example.lab_task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.TagEntity
import kotlinx.coroutines.launch

class ListViewModel: ViewModel() {
    val repository: TagRepository = TagRepository
    val tagsForDisplay: MutableLiveData<List<TagEntity>> = MutableLiveData()
    val helpingText = MutableLiveData<String>()
    val photoPathForOpenTag = MutableLiveData<String>()

    init {
        loadTags()
    }

    fun loadTags(){
        viewModelScope.launch {
            repository.getTags().collect{
                tagsForDisplay.postValue(it)
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

//    fun getPhotoPath(path: String){
//        photoPathForOpenTag.value = repository.getPhotoPath(path)
//    }

}