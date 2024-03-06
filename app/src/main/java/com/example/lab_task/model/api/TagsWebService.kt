package com.example.lab_task.model.api

import com.example.lab_task.model.api.entities.RegisterUser
import com.example.lab_task.model.api.entities.TagResponse
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.api.entities.UserResponse
import com.example.lab_task.model.UserAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object TagsWebService {
    var token: String = ""
    val url = "https://maps.rtuitlab.dev"
    private val api: TagsApi by lazy {
        createTagsApi()
    }

    suspend fun getTags(): Response<List<TagResponse>> {
        return api.getTags(getBearerToken())
    }

    suspend fun addTag(data: TransmittedTag): Response<TagResponse> {
        return api.addTag(data.latitude, data.longitude, data.description, data.image, getBearerToken())
    }

    suspend fun deleteTag(id: String): Response<String> {
        return api.deleteTag(id, getBearerToken())
    }

    private fun createTagsApi(): TagsApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(TagsApi::class.java)
    }

    suspend fun createAccount(username: String, password: String): Response<UserResponse>{
        return api.registerAccount(RegisterUser(username, password))
    }

    suspend fun auth(username: String, password: String): Response<UserAuth>{
        return api.authUser(username, password)
    }

    suspend fun addLike(tagId: String): Response<TagResponse>{
        return api.likeTag(tagId, getBearerToken())
    }

    suspend fun deleteLike(tagId: String): Response<String>{
        return api.deleteLike(tagId, getBearerToken())
    }

    suspend fun getPhoto(path: String): Response<String>{
        return api.getPhoto(path)
    }

    fun getBearerToken(): String = "Bearer $token"
}
