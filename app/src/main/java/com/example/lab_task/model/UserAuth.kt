package com.example.lab_task.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class UserAuth(
    val access_token: String,
    val token_type: String,
    @PrimaryKey val type: String = "auth_token"
)