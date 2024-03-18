package com.example.lab_task

import android.app.Application
import androidx.room.Room
import com.example.lab_task.model.sqlite.TagDatabase
import com.example.lab_task.notification.NotificationUtils
import com.example.lab_task.notification.setupWorkManager

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
}