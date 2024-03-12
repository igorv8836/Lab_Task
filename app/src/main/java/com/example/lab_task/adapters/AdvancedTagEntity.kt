package com.example.lab_task.adapters

import com.example.lab_task.model.sqlite.TagEntity

class AdvancedTagEntity(
    id: String,
    latitude: Double,
    longitude: Double,
    description: String,
    imagePath: String?,
    likes: Int,
    isLiked: Boolean,
    userId: String?,
    username: String?,
    val isAuthor: Boolean,
    val isSubscribed: Boolean
) : TagEntity(id, latitude, longitude, description, imagePath, likes, isLiked, userId, username) {
}