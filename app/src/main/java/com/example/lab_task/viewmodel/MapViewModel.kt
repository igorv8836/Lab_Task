package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.api.entities.TransmittedTag
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.TagEntity
import com.example.lab_task.view.MapPosition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class MapViewModel : ViewModel() {
    private val repository = TagRepository

    val startingPos: MutableLiveData<MapPosition> = MutableLiveData()
    val helpingText: MutableLiveData<String> = MutableLiveData()
    val tags: MutableLiveData<List<TagEntity>> = MutableLiveData()
    val openedTag: MutableLiveData<TagEntity> = MutableLiveData()
    val photoForNewTag: MutableLiveData<File?> = MutableLiveData()
    val showDeleteButton: MutableLiveData<Boolean> = MutableLiveData()
    val showSubscribeButton: MutableLiveData<Boolean> = MutableLiveData()

    init {
        initToken()
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
                checkOpenTag()
            }
        }
    }

    private fun showDeleteButton(tagId: String){
        viewModelScope.launch{
            val res = repository.checkIsAuthor(tagId).first()
            showDeleteButton.postValue(res)
        }
    }

    private fun showSubscribeButton(tagId: String){
        viewModelScope.launch{
            val res = repository.checkIsAuthor(tagId).first()
            showSubscribeButton.postValue(!res)
        }
    }

    fun openTagInfoFrame(tagId: String){
        findTagById(tagId)?.let {
            showDeleteButton(it.id)
            showSubscribeButton(it.id)
            openedTag.value = it
//            getPhotoPath(it.imagePath ?: "")
        }
    }

    private fun checkOpenTag(){
        val openTag = openedTag.value
        val foundTag = tags.value?.firstOrNull { it.id == openTag?.id }
        if (openTag != foundTag)
            foundTag.let { openedTag.value = it }
    }

    fun addTag(latitude: Double, longitude: Double, description: String){
        viewModelScope.launch {
            repository.addTag(TransmittedTag(latitude, longitude, description, photoForNewTag.value))
        }
    }

    private fun findTagById(tagId: String) = tags.value?.firstOrNull { it.id == tagId }

    fun changeLike(id: String){
        val foundTag = findTagById(id) ?: return
        viewModelScope.launch {
            if (!foundTag.isLiked) {
                repository.addLike(foundTag.id)
            }else {
                repository.deleteLike(foundTag.id)
            }
        }
    }

    fun setSelectedImage(file: File?){
        photoForNewTag.value = file
    }

    fun deleteTag(tagId: String){
        viewModelScope.launch {
            repository.deleteTag(tagId)
        }
    }
}