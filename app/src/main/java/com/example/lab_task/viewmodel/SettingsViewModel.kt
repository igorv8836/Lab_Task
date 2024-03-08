package com.example.lab_task.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.api.TagsWebService
import com.example.lab_task.model.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel : ViewModel() {
    private val webService = TagsWebService
    val snackbarText = MutableLiveData<String>()
    val token = MutableLiveData<String?>()
    val isAuthed = MutableLiveData<Boolean>()
    private val repository = TagRepository

    init {
        getAuthUser()
    }


    fun createAccount(username: String, password: String){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    webService.createAccount(username, password)
                }

                when (response.code()) {
                    201 -> {
                        snackbarText.value = "Успешно зарегистрирован аккаунт " + response.body()?.username
                    }

                    400 -> {
                        snackbarText.value = "Такой аккаунт существует или неверный пароль"
                    }

                    422 -> {
                        snackbarText.value = response.message()
                    }
                }
            } catch (e: Exception) {
                snackbarText.postValue("Критическая ошибка: ${e.message}")
                Log.i("api", e.message.toString())
            }
        }
    }

    fun getAuthUser(){
        viewModelScope.launch {
            repository.checkAuth().collect{
                if (it)
                    isAuthed.postValue(true)
                else
                    isAuthed.postValue(false)
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
            }
        }
    }

    fun logOut(){
        isAuthed.value = false
        viewModelScope.launch {
            repository.logOut()
        }
    }
}