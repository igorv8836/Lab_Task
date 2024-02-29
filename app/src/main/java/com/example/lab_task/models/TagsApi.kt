package com.example.lab_task.models

import retrofit2.http.GET

interface TagsApi {
    @GET("/api/tags/")
    suspend fun getTags(): List<Tag>
}