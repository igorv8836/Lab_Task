package com.example.lab_task.model.api.entities

data class TransmittedTag(
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val image: String?
)