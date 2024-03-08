package com.example.lab_task.model.api.entities

import java.io.File

data class TransmittedTag(
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val image: File?
)