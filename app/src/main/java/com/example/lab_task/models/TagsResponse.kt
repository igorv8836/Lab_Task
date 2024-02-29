package com.example.lab_task.models

import com.squareup.moshi.Json

data class TagsResponse(val tags: List<Tag>)

data class Tag(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val image: String?,
    @Json(name="is_liked") val isLiked: Boolean,
    val user: User?
)

data class User(val id: String, val username: String)


