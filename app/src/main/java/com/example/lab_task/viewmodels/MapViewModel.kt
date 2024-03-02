package com.example.lab_task.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.models.PostTag
import com.example.lab_task.models.Tag
import com.example.lab_task.models.TagsWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {
    private val tagsWebService = TagsWebService()
    val tags: MutableLiveData<List<Tag>> = MutableLiveData()
    val addedTag: MutableLiveData<Tag> = MutableLiveData()

    fun getTags(){
        viewModelScope.launch {
            try {
                val tagsResponse = withContext(Dispatchers.IO) {
                    tags.postValue(tagsWebService.getTags())
                    addAddedTagToMainList()
                    Log.i("api", tags.value?.size.toString())
                }

            } catch (e: Exception) {
                Log.i("api", tags.value?.size.toString())
            }
        }
    }

    fun addTag(latitude: Double, longitude: Double, description: String, image: String?){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    addedTag.postValue(tagsWebService.addTag(PostTag(latitude, longitude, description, image)))
                    Log.i("api_post", "Success")
                }
            } catch (e: Exception){
                Log.i("api_post", e.message.toString())
            }
        }
    }

    fun addAddedTagToMainList(){
        val tagValue = addedTag.value
        val tags = ArrayList(tags.value)
        tags.add(tagValue)
        this.tags.value = tags
    }
}