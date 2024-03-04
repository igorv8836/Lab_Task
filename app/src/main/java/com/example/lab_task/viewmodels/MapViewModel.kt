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
    val helpingText: MutableLiveData<String> = MutableLiveData()
    val tags: MutableLiveData<List<Tag>> = MutableLiveData()
    val changedLikeOfTag: MutableLiveData<Tag> = MutableLiveData()
    var username: String? = null
    private var token: String? = null


    fun addToken(token: String){
        tagsWebService.token = token
    }

    fun getTags(){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = tagsWebService.getTags()
                    if (response.code() == 422)
                        helpingText.postValue("Ошибка HTTP 422: ${response.message()}")
                    else
                        tags.postValue(response.body())
                }
            } catch (e: Exception) {
                helpingText.postValue("Критическая ошибка: ${e.message}")
                Log.i("api", e.message.toString())
            }
        }
    }

    fun addTag(latitude: Double, longitude: Double, description: String, image: String?){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = tagsWebService.addTag(PostTag(latitude, longitude, description, image))
                    if (response.code() == 422) {
                        helpingText.postValue("Ошибка HTTP 422: ${response.message()}")
                    } else{
                        val tags = ArrayList(tags.value)
                        tags.add(response.body())
                        this@MapViewModel.tags.postValue(tags)
                    }
                }
            } catch (e: Exception){
                helpingText.postValue("Критическая ошибка: ${e.message}")
                Log.i("api_post", e.message.toString())
            }
        }
    }

    fun auth(username: String, password: String){
        viewModelScope.launch {
            try {
                val response = tagsWebService.auth(username, password)
                when(response.code()){
                    200 -> {
                        token = response.body()?.access_token
                    }
                    400 -> {
                        helpingText.postValue(
                            "Неверный логин или пароль или пользователь не верифицирован"
                        )
                    }
                    422 -> {
                        helpingText.postValue("Ошибка HTTP 422: ${response.message()}")
                    }
                }
            } catch (e: Exception){
                helpingText.postValue("Критическая ошибка: ${e.message}")
                Log.i("auth_api", e.message.toString())
            }
        }
    }

    fun changeLike(tagId: String){
        val foundTag = findTagById(tagId) ?: return
        if (!foundTag.isLiked){
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val response = tagsWebService.addLike(tagId)

                        when (response.code()){
                            201 -> {
                                val arr: ArrayList<Tag> = ArrayList(tags.value)
                                for (i in arr)
                                    if (i.id == response.body()?.id) {
                                        i.isLiked = true
                                        i.likes++
                                        break
                                    }
                                tags.postValue(arr)
                                changedLikeOfTag.postValue(response.body())
                            }
                            422 -> {
                                helpingText.postValue("Ошибка HTTP 422: ${response.message()}")
                            }
                        }
                    }
                } catch (e: Exception){
                    helpingText.postValue("Критическая ошибка: ${e.message}")
                    Log.i("api_like", e.message.toString())
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val response = tagsWebService.deleteLike(tagId)

                        when (response.code()){
                            204 -> {
                                val arr: ArrayList<Tag> = ArrayList(tags.value)
                                for (i in arr)
                                    if (i.id == tagId) {
                                        i.isLiked = false
                                        i.likes--
                                        changedLikeOfTag.postValue(i)
                                        break
                                    }
                                tags.postValue(arr)
                            }
                            422 -> {
                                helpingText.postValue("Ошибка HTTP 422: ${response.message()}")
                            }
                        }
                    }
                } catch (e: Exception){
                    helpingText.postValue("Критическая ошибка: ${e.message}")
                    Log.i("api_like_delete", e.message.toString())
                }
            }
        }
    }

    fun findTagByCoord(latitude: Double, longitude: Double): Tag?{
        return tags.value?.firstOrNull {
            it.latitude == latitude && it.longitude == longitude
        }
    }

    fun findTagById(tagId: String): Tag? {
        return tags.value?.firstOrNull {
            it.id == tagId
        }
    }

    fun addAuthedTag(latitude: Double, longitude: Double, description: String, image: String?, token: String){
        viewModelScope.launch {
            try {
                val response = tagsWebService.addAuthTag(
                    PostTag(55.662882, 37.485610, "tteess", null),
                    token
                    )
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
                helpingText.postValue("Критическая ошибка: ${e.message}")
                Log.i("delete_api", e.message.toString())
            }
        }
    }

    fun updateToken(){

    }
}