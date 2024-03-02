package com.example.lab_task.models

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TagsWebService {
    private val url = "https://maps.rtuitlab.dev"
    private val api: TagsApi by lazy {
        createTagsApi()
    }

    suspend fun getTags(): List<Tag> {
        return api.getTags()
    }

    suspend fun addTag(data: PostTag): Tag {
        return api.addTag(data.latitude, data.longitude, data.description)
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
}
