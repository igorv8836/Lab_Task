package com.example.lab_task.model.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.lab_task.App
import com.example.lab_task.R
import com.example.lab_task.model.api.TagsWebService
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.sqlite.TokenEntity
import com.example.lab_task.model.sqlite.UserEntity
import com.example.lab_task.view.MapPosition
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object TagRepository {
    private val api = TagsWebService
    private val database = App.dataBase.getTagDao()
    val errorMessage = MutableSharedFlow<String?>()

    suspend fun getTags(): Flow<List<TagEntity>> {
        try {
            withContext(Dispatchers.IO) {
                val response = api.getTags()
                when(response.code()){
                    200 -> database.insertTags(response.body()!!.map {
                            val temp = it
                            temp.image.let { temp.image = api.url + temp.image }
                            TagEntity(temp)
                        })
                    else -> errorMessage.emit("${response.code()}: ${response.message()}") }
            }
        } catch (e: Exception){
            errorMessage.emit("Error: ${e.message.toString()}")
        }
        return database.getTags()
    }

        suspend fun checkAuth() = flow {
                val user: UserEntity? = database.getUser().first()
                emit(user?.username)
                }.flowOn(Dispatchers.IO)

    suspend fun setTokenFromLocal(){
        try {
            withContext(Dispatchers.IO) {
                val tk = database.getToken()
                if (tk != null)
                    api.token = tk.access_token
            }
        } catch (e: Exception){
            errorMessage.emit("Error: ${e.message.toString()}")
        }
    }

    suspend fun addTag(data: TransmittedTag) {
        try {
            withContext(Dispatchers.IO) {
                val response = api.addTag(data)
                when (response.code()) {
                    201 -> database.insertTag(TagEntity(response.body()!!))
                    else -> errorMessage.emit("${response.code()}: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            errorMessage.emit("Error: ${e.message.toString()}")
        }
    }

    suspend fun auth(username: String, password: String): Flow<String>  {
        return flow {
            try {
                val response = api.auth(username, password)
                when (response.code()) {
                    200 -> {
                        response.body()?.let { TokenEntity(it) }?.let { database.insertToken(it) }
                        response.body()?.let { TokenEntity(it) }?.let { database.insertUser(
                            UserEntity("", username)
                        ) }
                        emit(username)
                        api.token = response.body()?.access_token ?: ""
                    }
                    400 -> errorMessage.emit("Неправильный логин или пароль")
                    else -> errorMessage.emit("${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) { errorMessage.emit("Error: ${e.message.toString()}") }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addLike(tagId: String) {
         withContext(Dispatchers.IO) {
            try {
                val response = api.addLike(tagId)
                when (response.code()) {
                    201 -> {
                        response.body()?.let { TagEntity(it) }?.let {
                            val temp = it
                            temp.imagePath = api.url + temp.imagePath
                            database.insertTag(temp)
                        }
                    }
                    else -> {
                        errorMessage.emit("${response.code()}: ${response.message()}")
                    }
                }
            } catch (e: Exception) { errorMessage.emit("Error: ${e.message.toString()}") }
        }
    }

    suspend fun deleteLike(tagId: String) {
         withContext(Dispatchers.IO) {
            try {
                val response = api.deleteLike(tagId)
                when (response.code()) {
                    204 -> {
                        database.getTag(tagId).first {
                            it.likes--
                            it.isLiked = !it.isLiked
                            database.insertTag(it)
                            true
                        }
                    }
                    else -> errorMessage.emit("${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) { errorMessage.emit("Error: ${e.message.toString()}") }
        }
    }

    suspend fun logOut(){
        withContext(Dispatchers.IO) {
            database.deleteToken()
            database.deleteUser()
            api.token = ""
        }
    }

//    fun getPhotoPath(path: String) = "${api.url}$path"

    fun getStartingPos(): Flow<MapPosition>{
        return flow<MapPosition> {
            val a = App.instance.getSharedPreferences("basic", Context.MODE_PRIVATE)
            with(a){
                emit(MapPosition(
                    getFloat("latitude", 55.0f).toDouble(),
                    getFloat("longitude", 37.0f).toDouble(),
                    getFloat("zoom", 17.0f).toDouble(),
                    getFloat("azimuth", 0.0f).toDouble(),
                    getFloat("tilt", 0.0f).toDouble()
                ))
            }
        }
    }

    fun setStartingPos(coord: MapPosition){
        val a = App.instance.getSharedPreferences("basic", Context.MODE_PRIVATE)
        with(a.edit()){
            putFloat("latitude", coord.latitude.toFloat())
            putFloat("longitude", coord.longitude.toFloat())
            putFloat("zoom", coord.zoom.toFloat())
            putFloat("azimuth", coord.azimuth.toFloat())
            putFloat("tilt", coord.tilt.toFloat())
            apply()
        }
    }

    suspend fun deleteTag(id: String) {
        withContext(Dispatchers.IO){
            val response = api.deleteTag(id)

            when (response.code()){
                204 ->database.deleteTag(id)
                else -> errorMessage.emit("Вы не автор метки или вы не авторизованы")
            }
        }
    }

    suspend fun createAccount(username: String, password: String) {
        try {
            val response = withContext(Dispatchers.IO) { api.createAccount(username, password) }
            when (response.code()) {
                201 -> errorMessage.emit("Успешно зарегистрирован аккаунт " + response.body()?.username)
                400 -> errorMessage.emit("Такой аккаунт существует или неверный пароль")
                422 -> errorMessage.emit(response.message())
            }
        } catch (e: Exception) {
            errorMessage.emit("Критическая ошибка: ${e.message}")
            Log.i("api", e.message.toString())
        }
    }

    suspend fun checkIsAuthor(id: String) = flow<Boolean> {
        val username = database.getUser().first()
        val tag = database.getTag(id).first()

        if (tag == null || username == null || tag.username == null) {
            emit(false)
        } else {
            emit(username.username == tag.username)
        }
    }.flowOn(Dispatchers.IO)
}