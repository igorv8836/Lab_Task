package com.example.lab_task.model.api.entities

import com.squareup.moshi.Json

data class TagResponse(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val image: String?,
    var likes: Int,
    @Json(name="is_liked") var isLiked: Boolean,
    val user: UserResponse?
)

data class UserResponse(val id: String, val username: String, val type: String = "account")


