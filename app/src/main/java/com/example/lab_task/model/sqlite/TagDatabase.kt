package com.example.lab_task.model.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TagEntity::class,
        TokenEntity::class,
        UserEntity::class
    ]
)
abstract class TagDatabase : RoomDatabase() {
    abstract fun getTagDao(): TagDao
}