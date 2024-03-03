package com.example.lab_task.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_task.models.TagsWebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel : ViewModel() {
    private val webService = TagsWebService
    val snackbarText = MutableLiveData<String>()


    fun createAccount(username: String, password: String){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    webService.createAccount(username, password)
                }

                when (response.code()) {
                    201 -> {
                        snackbarText.value = response.body()?.username
                    }

                    400 -> {
                        snackbarText.value = "Такой аккаунт существует"
                    }

                    422 -> {
//                        snackbarText.value = response.message()
                    }
                }
            } catch (e: Exception) {
                snackbarText.value = "Критическая ошибка"
                Log.i("api", e.message.toString())
            }
        }
    }
}