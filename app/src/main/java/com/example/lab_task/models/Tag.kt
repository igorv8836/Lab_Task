package com.example.lab_task.models

import com.squareup.moshi.Json


data class Tag(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val image: String?,
    var likes: Int,
    @Json(name="is_liked") var isLiked: Boolean,
    val user: User?
)

data class User(val id: String, val username: String)


