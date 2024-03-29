package com.example.lab_task.model.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TagEntity) return false
        return id == other.id && likes == other.likes && isLiked == other.isLiked
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (imagePath?.hashCode() ?: 0)
        result = 31 * result + likes
        result = 31 * result + isLiked.hashCode()
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }
}


@Entity(tableName = "user")
data class UserEntity(val id: String, val username: String, @PrimaryKey val type: String = "account")

@Entity(tableName = "tokens")
data class TokenEntity(
    var access_token: String,
    val token_type: String,
    @PrimaryKey val type: String = "auth_token"
){
    constructor(userToken: UserAuth): this(
        userToken.access_token,
        userToken.token_type
    )
}

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey val user_id: String,
    val last_tags: String? = null
)