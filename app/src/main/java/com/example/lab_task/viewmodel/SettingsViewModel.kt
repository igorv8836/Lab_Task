package com.example.lab_task.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.model.api.TagsWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel : ViewModel() {
    private val webService = TagsWebService
    val snackbarText = MutableLiveData<String>()
    val token = MutableLiveData<String?>()
    val username = MutableLiveData<String>()


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

    fun authAccount(username: String, password: String){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO){
                    val response = webService.auth(username, password)

                    when(response.code()){
                        200 -> {
                            token.postValue(response.body()?.access_token)
                            this@SettingsViewModel.username.postValue(username)
                        }
                        400 -> {
                            snackbarText.postValue(
                                "Неверный логин или пароль или пользователь не верифицирован"
                            )
                        }
                        422 -> {
                            snackbarText.postValue("Ошибка HTTP 422: ${response.message()}")
                        }
                    }
                }
            } catch (e: Exception){
                snackbarText.postValue("Критическая ошибка: ${e.message}")
                Log.i("logIn", e.message.toString())
            }
        }
    }

    fun logOut(){
        username.value = "Не авторизован"
        token.value = null
    }
}