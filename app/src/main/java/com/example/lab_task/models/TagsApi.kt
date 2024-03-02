package com.example.lab_task.models

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface TagsApi {
    @GET("/api/tags/")
    suspend fun getTags(): List<Tag>


    @POST("/api/tags/")
    suspend fun addTag(@Body data: PostTag): Tag
}