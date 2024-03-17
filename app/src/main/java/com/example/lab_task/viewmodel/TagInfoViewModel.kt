package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.Subscription
import com.example.lab_task.model.sqlite.TagEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TagInfoViewModel: ViewModel() {
    private val repository = TagRepository
    val tag = MutableLiveData<TagEntity?>()
    val showDeleteButton = MutableLiveData<Boolean>()
    private var currUser: String? = null
    var isSubscribed = MutableLiveData<Boolean>()

    init {
        getUser()
    }


    fun getTag(tagId: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repository.getTag(tagId).collect{
                    tag.postValue(it)
                }
            }
        }
    }

    private fun getUser(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getCurrUser().collect{ currUser = it?.username }
        }
    }

    fun showDeleteButton(){
        if (currUser != null && currUser == tag.value?.username)
            showDeleteButton.postValue(true)
        else
            showDeleteButton.postValue(false)
    }

    fun changeLike(){
        viewModelScope.launch {
            if (tag.value?.isLiked == false) {
                tag.value?.id?.let { repository.addLike(it) }
            }else {
                tag.value?.id?.let { repository.deleteLike(it) }
            }
        }
    }

    fun checkSubscription(){
        viewModelScope.launch(Dispatchers.IO) {
            tag.value?.userId?.let { s ->
                repository.getSubscription(s).collect{
                    isSubscribed.postValue(it != null)
                }
            }
        }
    }

    fun subscribeButton(){
        viewModelScope.launch(Dispatchers.IO) {
            tag.value?.userId?.let {
                if (isSubscribed.value == false)
                    repository.addSubscription(Subscription(it))
                else
                    repository.deleteSubscription(Subscription(it))
            }
        }
    }

    fun delete(){
        viewModelScope.launch(Dispatchers.IO) {
            tag.value?.id?.let{ repository.deleteTag(it) }
        }
    }
}