package com.example.lab_task.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.api.entities.TagResponse
import com.example.lab_task.model.api.TagsWebService
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.repository.MessageListener
import com.example.lab_task.model.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MapViewModel : ViewModel() {
    private val repository = TagRepository
    private val tagsWebService = TagsWebService
    val helpingText: MutableLiveData<String> = MutableLiveData()
    val tags: MutableLiveData<List<TagResponse>> = MutableLiveData()
    val changedLikeOfTag: MutableLiveData<TagResponse> = MutableLiveData()
    var username: String? = null
    private var token: String? = null
    val photoForNewTag: MutableLiveData<File?> = MutableLiveData()


    fun addToken(token: String){
        tagsWebService.token = token
    }

    fun getTags(){
        repository.getTags()
    }

    fun addTag(latitude: Double, longitude: Double, description: String, image: Bitmap?){
        viewModelScope.launch {
            repository.addTag(
                TransmittedTag(latitude, longitude, description, null),
                object : MessageListener{
                    override fun sendMessage(message: String) {
                        helpingText.postValue(message)
                    }

                }
            )
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
                                val arr: ArrayList<TagResponse> = ArrayList(tags.value)
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
                                val arr: ArrayList<TagResponse> = ArrayList(tags.value)
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

    fun findTagByCoord(latitude: Double, longitude: Double): TagResponse?{
        return tags.value?.firstOrNull {
            it.latitude == latitude && it.longitude == longitude
        }
    }

    fun findTagById(tagId: String): TagResponse? {
        return tags.value?.firstOrNull {
            it.id == tagId
        }
    }

    fun getPhoto(path: String): String {
        return tagsWebService.url + path
    }

    fun setImage(file: File?){
        photoForNewTag.value = file
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