package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.adapters.SubscribedTag
import com.example.lab_task.model.other.ChangeChecker
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.Subscription
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.model.other.FiltersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    private var filters: FiltersData? = null
    private var loadedTags: List<TagEntity> = emptyList()
    val repository: TagRepository = TagRepository
    val tagsForDisplay: MutableLiveData<List<TagEntity>> = MutableLiveData()
    val helpingText = MutableLiveData<String>()
    val currUsername = MutableLiveData<String?>()

    init {
        loadTags()
        getCurrUserId()
        checkSubscriptions()
        getErrorMessage()
        checkChanges()
    }

    private fun loadTags() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTags().collect{
                val newList = ArrayList<TagEntity>()
                val subs = repository.getSubscriptions().first()
                for (i in it.indices){
                    val sub = subs.firstOrNull { it1 -> it1.user_id == it[i].userId }
                    newList.add(SubscribedTag(it[i], sub != null))
                }
                loadedTags = newList
                updateFilteredList()
            }
        }
    }

    private fun checkSubscriptions(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSubscriptions().collect{
                loadTags()
            }
        }
    }

    private fun updateFilteredList() {
        viewModelScope.launch {
            val newList = loadedTags.filter { tag ->
                val searchText = filters?.searchText.orEmpty()
                val satisfiesSearchCriteria = when (filters?.typeSearch) {
                    0 -> tag.username?.contains(searchText, ignoreCase = true) ?: false ||
                            tag.description.contains(searchText, ignoreCase = true)
                    1 -> tag.username?.contains(searchText, ignoreCase = true) ?: false
                    2 -> tag.description.contains(searchText, ignoreCase = true)
                    else -> true
                }
                satisfiesSearchCriteria && (!(filters?.onlyWithPhoto ?: false) && tag.imagePath == null || tag.imagePath != null)
            }

            val sortedList = when (filters?.sortType) {
                0 -> newList.sortedByDescending { it.likes }
                1 -> newList.sortedBy { it.likes }
                2 -> newList.sortedBy { it.username }
                3 -> newList.sortedByDescending { it.username }
                else -> newList
            }

            tagsForDisplay.postValue(sortedList)
        }
    }

    fun subscribeButton(userId: String, isSubscribed: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if (!isSubscribed)
                repository.addSubscription(Subscription(userId))
            else
                repository.deleteSubscription(Subscription(userId))
        }
    }

    fun applyFilters(data: FiltersData?) {
        filters = data
        updateFilteredList()
    }

    fun searchInText(text: String) {
        if (filters == null)
            filters = FiltersData(-1, false)
        filters?.searchText = text
        updateFilteredList()
    }

    fun getCurrUserId() {
        viewModelScope.launch {
            repository.getCurrUser().collect {
                currUsername.postValue(it?.username)
            }
        }

    }

    fun getErrorMessage() {
        viewModelScope.launch {
            repository.errorMessage.collect {
                if (it != null)
                    helpingText.postValue(it)
            }
        }
    }

    fun changeLike(id: String, isLiked: Boolean) {
        viewModelScope.launch {
            if (!isLiked) {
                repository.addLike(id)
            } else {
                repository.deleteLike(id)
            }
        }
    }

    fun deleteTag(tagId: String) {
        viewModelScope.launch {
            repository.deleteTag(tagId)
        }
    }

    private fun checkChanges(){
        viewModelScope.launch(Dispatchers.IO) {
            ChangeChecker.check(repository)
        }
    }

}