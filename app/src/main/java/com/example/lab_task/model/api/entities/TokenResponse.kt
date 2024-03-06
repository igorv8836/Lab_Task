package com.example.lab_task.model.api.entities

data class TokenResponse(
    val access_token: String,
    val token_type: String
)