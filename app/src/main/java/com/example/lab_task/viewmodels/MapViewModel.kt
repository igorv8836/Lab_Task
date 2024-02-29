package com.example.lab_task.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.models.Tag
import com.example.lab_task.models.TagsWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {
    private val tagsWebService = TagsWebService()
    val tags: MutableLiveData<List<Tag>> = MutableLiveData()

    fun getTags(){
        viewModelScope.launch {
            try {
                val tagsResponse = withContext(Dispatchers.IO) {
                    tags.postValue(tagsWebService.getTags())
                    Log.i("api", tags.value?.size.toString())
                }

            } catch (e: Exception) {
                Log.i("api", tags.value?.size.toString())
            }
        }
    }
}