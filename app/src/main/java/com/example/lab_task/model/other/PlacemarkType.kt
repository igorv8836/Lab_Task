package com.example.lab_task.model.other

enum class PlacemarkType {
    YELLOW,
    GREEN,
    RED
}

data class PlacemarkUserData(
    val name: String,
    val type: PlacemarkType,
)
