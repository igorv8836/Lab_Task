package com.example.lab_task.model.other

data class FiltersData(
    val sortType: Int,
    val onlyWithPhoto: Boolean,
    val typeSearch: Int = 0,
    var searchText: String = "")