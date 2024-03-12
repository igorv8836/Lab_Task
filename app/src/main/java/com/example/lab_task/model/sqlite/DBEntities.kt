package com.example.lab_task.model.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lab_task.model.UserAuth
import com.example.lab_task.model.api.entities.TagResponse
import kotlin.math.min

@Entity(tableName = "tags")
open class TagEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    var imagePath: String?,
    var likes: Int,
    var isLiked: Boolean,
    val userId: String?,
    val username: String?
) {
    constructor(tag: TagResponse) : this(
        tag.id,
        tag.latitude,
        tag.longitude,
        tag.description,
        tag.image,
        tag.likes,
        tag.isLiked,
        tag.user?.id,
        tag.user?.username
    )

    fun getFormatCoord(): String{
        val lat = latitude.toString()
        val lon = longitude.toString()
        return "${lat.substring(0, min(lat.length, 9))}, ${lon.substring(0, min(lon.length, 9))}"
    }

    fun getImageUrl() = "\"https://maps.rtuitlab.dev\"${imagePath}"
}


@Entity(tableName = "user")
data class UserEntity(val id: String, val username: String, @PrimaryKey val type: String = "account")

@Entity(tableName = "tokens")
data class TokenEntity(
    val access_token: String,
    val token_type: String,
    @PrimaryKey val type: String = "auth_token"
){
    constructor(userToken: UserAuth): this(
        userToken.access_token,
        userToken.token_type
    )
}