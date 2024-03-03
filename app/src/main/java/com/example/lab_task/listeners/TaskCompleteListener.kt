package com.example.lab_task.listeners

interface TaskCompleteListener {

    fun onSuccessFinished()
    fun onErrorFinished(error: String)
}