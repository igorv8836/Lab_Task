package com.example.lab_task

import android.app.Application
import androidx.room.Room
import com.example.lab_task.model.sqlite.TagDatabase

class App: Application() {
    companion object{
        lateinit var instance: App
        lateinit var dataBase: TagDatabase
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        dataBase = Room.databaseBuilder(this, TagDatabase::class.java,"mainDatabase").build()
    }

    fun getInstance() = instance
    fun getDatabase() = dataBase
}