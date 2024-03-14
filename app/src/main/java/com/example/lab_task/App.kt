package com.example.lab_task

import android.app.Application
import androidx.room.Room
import com.example.lab_task.model.sqlite.TagDatabase
import com.example.lab_task.notifications.NotificationUtils
import com.example.lab_task.notifications.setupWorkManager

class App: Application() {
    companion object{
        lateinit var instance: App
        lateinit var dataBase: TagDatabase
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        dataBase = Room.databaseBuilder(this, TagDatabase::class.java,"mainDatabase").build()

        NotificationUtils.createNotificationChannel(applicationContext)
        setupWorkManager(applicationContext)
    }

    fun getInstance() = instance
    fun getDatabase() = dataBase
}