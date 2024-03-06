package com.example.lab_task.model.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lab_task.model.api.entities.TagResponse

@Entity(tableName = "tags")
class TagEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val imagePath: String?,
    val likes: Int,
    val isLiked: Boolean,
    val userId: String?
) {

    constructor(tag: TagResponse) : this(
        tag.id,
        tag.latitude,
        tag.longitude,
        tag.description,
        tag.image,
        tag.likes,
        tag.isLiked,
        tag.user?.id
    ) {

    }
}


@Entity(tableName = "user")
data class UserEntity(val id: String, val username: String, @PrimaryKey val type: String = "account")

@Entity(tableName = "tokens")
data class TokenEntity(
    val access_token: String,
    val token_type: String,
    @PrimaryKey val type: String = "auth_token"
)