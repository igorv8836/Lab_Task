package com.example.lab_task.models

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TagsApi {
    @GET("/api/tags/")
    suspend fun getTags(): List<Tag>

    @Multipart
    @POST("/api/tags/")
    suspend fun addTag(
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double,
        @Part("description") description: String
    ): Tag
}