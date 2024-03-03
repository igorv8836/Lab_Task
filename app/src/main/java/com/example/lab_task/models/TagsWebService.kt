package com.example.lab_task.models

import android.hardware.usb.UsbRequest
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object TagsWebService {
    private var token: String? = null
    private val url = "https://maps.rtuitlab.dev"
    private val api: TagsApi by lazy {
        createTagsApi()
    }

    suspend fun getTags(): Response<List<Tag>> {
        return api.getTags()
    }

    suspend fun addTag(data: PostTag): Response<Tag> {
        return api.addTag(data.latitude, data.longitude, data.description)
    }

    suspend fun deleteTag(id: String, token: String): Response<String> {
        return api.deleteTag(id, "Bearer $token")
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

    suspend fun createAccount(username: String, password: String): Response<User>{
        return api.registerAccount(RegisterUser(username, password))
    }

    suspend fun auth(username: String, password: String): Response<UserAuth>{
        return api.authUser(username, password)
    }

    suspend fun addAuthTag(data: PostTag, auth: String): Response<Tag> {
        return api.addAuthTag(data.latitude, data.longitude, data.description, "Bearer $auth")
    }

//    suspend fun test(token: String): Response<UserAuth>{
//        return api.test(token)
//    }
}
