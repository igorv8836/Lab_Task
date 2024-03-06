package com.example.lab_task.model.sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lab_task.model.api.entities.UserResponse
import com.example.lab_task.model.UserAuth
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun getTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id")
    fun getTag(id: String): Flow<TagEntity>

    @Insert()
    fun insertTag(tag: TagEntity)

    @Update()
    fun updateTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :id")
    fun deleteTag(id: String)



    @Query("SELECT * FROM user WHERE type = 'account'")
    fun getUser(): Flow<UserResponse>

    @Insert()
    fun insertUser(tag: UserResponse)

    @Update()
    fun updateUser(tag: UserResponse)

    @Delete
    fun deleteUser(tag: UserResponse)

    @Query("SELECT * FROM tokens WHERE type = 'auth_token'")
    fun getToken(): Flow<UserAuth>


    @Insert()
    fun insertToken(tag: UserAuth)

    @Update()
    fun updateToken(tag: UserAuth)

    @Delete
    fun deleteToken(tag: UserAuth)
}