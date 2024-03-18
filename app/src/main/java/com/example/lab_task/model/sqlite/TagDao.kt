package com.example.lab_task.model.sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun getTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id")
    fun getTag(id: String): Flow<TagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: TagEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(tags: List<TagEntity>)

    @Query("DELETE FROM tags WHERE id = :id")
    fun deleteTag(id: String)

    @Query("DELETE FROM tags")
    fun clearTags()

    @Query("SELECT * FROM user WHERE type = 'account'")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(tag: UserEntity)

    @Query("DELETE FROM user WHERE type = 'account'")
    fun deleteUser()

    @Query("SELECT * FROM tokens WHERE type = 'auth_token'")
    fun getToken(): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(tag: TokenEntity)

    @Query("DELETE FROM tokens WHERE type = 'auth_token'")
    fun deleteToken()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSubscription(data: Subscription)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSubscriptions(data: List<Subscription>)

    @Delete
    fun deleteSubscription(data: Subscription)

    @Query("SELECT * FROM subscriptions")
    fun getSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE user_id = :user_id")
    fun getSubscription(user_id: String): Flow<Subscription?>
}