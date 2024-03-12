package com.example.lab_task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.repository.TagRepository
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    val snackbarText = MutableLiveData<String>()
    val isAuthed = MutableLiveData<Boolean>()
    val username = MutableLiveData<String>()
    private val repository = TagRepository

    init {
        getAuthUser()
        getErrorMessage()
    }


    fun createAccount(username: String, password: String){
        viewModelScope.launch {
            repository.createAccount(username, password)
        }
    }

    fun getAuthUser(){
        viewModelScope.launch {
            repository.checkAuth().collect{
                if (it != null)
                    username.value = it
            }
        }
    }

    fun getErrorMessage(){
        viewModelScope.launch {
            repository.errorMessage.collect{
                snackbarText.postValue(it)
            }
        }
    }

    fun authAccount(username: String, password: String){
        viewModelScope.launch {
            repository.auth(username, password).collect() {
                this@SettingsViewModel.isAuthed.postValue(true)
                this@SettingsViewModel.username.postValue(username)
            }
        }
    }

    fun logOut(){
        isAuthed.value = false
        username.value = "Не авторизован"
        viewModelScope.launch {
            repository.logOut()
        }
    }
}