package com.example.lab_task.model.repository

import android.util.Log
import com.example.lab_task.App
import com.example.lab_task.model.api.TagsWebService
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.model.api.entities.TagResponse
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.api.entities.UserResponse
import com.example.lab_task.model.UserAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response

object TagRepository {
    val api = TagsWebService
    val database = App.dataBase.getTagDao()
    var messageListener: MessageListener? = null

    fun getTags(): Flow<List<TagEntity>> {
        return database.getTags()
    }

    fun setListener(listener: MessageListener){
        messageListener = listener
    }

    suspend fun addTag(data: TransmittedTag) {
        try {
            withContext(Dispatchers.IO) {
                val response = api.addTag(data)
                when(response.code()){
                    201 -> App.dataBase.getTagDao().insertTag(TagEntity(response.body()!!))
                    422 -> messageListener?.sendMessage("Ошибка HTTP 422: ${response.message()}")
                    else -> messageListener?.sendMessage("Ошибка с null response body")
                }
            }
        } catch (e: Exception) {
            messageListener?.sendMessage("Критическая ошибка: ${e.message}")
            Log.e("api", e.message.toString())
        }
    }

    suspend fun deleteTag(id: String) {
        try{
            withContext(Dispatchers.IO){
                val response = api.deleteTag(id)
                when(response.code()){
                    422 -> messageListener?.sendMessage("Ошибка HTTP 422: ${response.message()}")
                    201 -> database.deleteTag(id)
                    else -> messageListener?.sendMessage("Ошибка")
                }
            }
        } catch (e: Exception){
            messageListener?.sendMessage("Критическая ошибка: ${e.message}")
            Log.i("delete_api", e.message.toString())
        }
    }

    suspend fun createAccount(username: String, password: String) {
        try {

        }catch (e: Exception)
    }

    suspend fun auth(username: String, password: String): Response<UserAuth> {

    }

    suspend fun addLike(tagId: String): Response<TagResponse> {

    }

    suspend fun deleteLike(tagId: String): Response<String> {

    }

    suspend fun getPhoto(path: String): Response<String> {

    }
}