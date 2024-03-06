package com.example.lab_task.model.repository

interface MessageListener {
    fun sendMessage(message: String)
}