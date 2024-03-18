package com.example.lab_task.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.lab_task.App
import com.example.lab_task.model.ChangeChecker
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.Subscription
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.notifications.NotificationUtils
import com.example.lab_task.notifications.UpdateTagsWorker
import com.example.lab_task.view.MapPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class MapViewModel : ViewModel() {
    private val repository = TagRepository

    val startingPos: MutableLiveData<MapPosition> = MutableLiveData()
    val helpingText: MutableLiveData<String> = MutableLiveData()
    val tags: MutableLiveData<List<TagEntity>> = MutableLiveData()
    val photoForNewTag: MutableLiveData<File?> = MutableLiveData()

    init {
        initToken()
        checkChanges()
    }

    fun getStartingPos(){
        viewModelScope.launch{
            repository.getStartingPos().collect{
                startingPos.postValue(it)
            }
        }
    }

    fun setStartingPos(position: MapPosition){
        repository.setStartingPos(position)
    }


    private fun initToken(){
        viewModelScope.launch {
            repository.setTokenFromLocal()
        }
    }

    fun getErrorMessage(){
        viewModelScope.launch {
            repository.errorMessage.collect{
                if (it != null)
                    helpingText.postValue(it)
            }
        }
    }

    fun getTags(){
        viewModelScope.launch{
            repository.getTags().collect{
                tags.value = it
            }
        }
    }

    fun addTag(latitude: Double, longitude: Double, description: String){
        viewModelScope.launch {
            repository.addTag(TransmittedTag(latitude, longitude, description, photoForNewTag.value))
        }
    }

    fun setSelectedImage(file: File?){
        photoForNewTag.value = file
    }

    private fun checkChanges(){
        viewModelScope.launch(Dispatchers.IO) {
            ChangeChecker.doWork(repository)
        }
    }
}