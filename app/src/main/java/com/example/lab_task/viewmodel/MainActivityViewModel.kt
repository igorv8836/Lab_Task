package com.example.lab_task.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.other.ChangeChecker
import com.example.lab_task.model.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel(){
    val repository = TagRepository
}