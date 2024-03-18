package com.example.lab_task.model.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserAuth(
    val access_token: String,
    val token_type: String,
    val type: String = "auth_token"
)