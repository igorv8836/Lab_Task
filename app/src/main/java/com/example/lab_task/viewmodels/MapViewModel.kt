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
    private val tagsWebService = TagsWebService
    val tags: MutableLiveData<List<Tag>> = MutableLiveData()
    val addedTag: MutableLiveData<Tag> = MutableLiveData()
    private var token: String? = null

    fun getTags(){
        viewModelScope.launch {
            try {
                val tagsResponse = withContext(Dispatchers.IO) {
                    tags.postValue(tagsWebService.getTags().body())
                }
            } catch (e: Exception) {
                Log.i("api", e.message.toString())
            }
        }
    }

    fun addTag(latitude: Double, longitude: Double, description: String, image: String?){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    addedTag.postValue(tagsWebService.addTag(PostTag(latitude, longitude, description, image)).body())
                }
            } catch (e: Exception){
                Log.i("api_post", e.message.toString())
            }
        }
    }

    fun addAddedTagToMainList(){
        val tags = ArrayList(tags.value)
        tags.add(addedTag.value)
        this.tags.value = tags
    }

    fun auth(username: String, password: String){
        viewModelScope.launch {
            try {
                val response = tagsWebService.auth(username, password)
                token = response.body()?.access_token
            } catch (e: Exception){
                Log.i("auth_api", e.message.toString())
            }
        }
    }

    fun addAuthedTag(latitude: Double, longitude: Double, description: String, image: String?, token: String){
        viewModelScope.launch {
            try {
                val response = tagsWebService.addAuthTag(
                    PostTag(55.662882, 37.485610, "tteess", null),
                    token
                    )
                addedTag.postValue(response.body())
                Log.i("api_auth_tag", response.message())
            } catch (e: Exception){
                Log.i("api_auth_tag", e.message.toString())
            }
        }
    }

    fun deleteTag(tagId: String){
        viewModelScope.launch {
            try{
                val response = tagsWebService.deleteTag(tagId, token ?: "")
                Log.i("delete_api", response.message())
            } catch (e: Exception){
                Log.i("delete_api", e.message.toString())
            }
        }
    }

    fun updateToken(){

    }
}