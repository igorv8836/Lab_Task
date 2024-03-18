package com.example.lab_task.notification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.lab_task.App
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.Subscription
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class UpdateTagsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val repository = TagRepository

    override suspend fun doWork(): Result {
        try {
            val newTags = repository.getTags().first()
            val subs = repository.getSubscriptions().first()
            val lastTagsMap = subs.associateBy(
                keySelector = { it.user_id },
                valueTransform = { subscription ->
                    subscription.last_tags?.split(",")?.mapNotNull { tagId ->
                        newTags.firstOrNull { it.id == tagId }?.id
                    }?.toMutableList() ?: mutableListOf()
                }
            )
            newTags.forEach { newTag ->
                if (newTag.userId in lastTagsMap && newTag.id !in lastTagsMap[newTag.userId] ?: emptyList()) {
                    lastTagsMap[newTag.userId]?.add(newTag.id)
                    NotificationUtils.showNotification(
                        App.instance,
                        "Новая метка",
                        "Пользователь ${newTag.username} опубликовал новую метку"
                    )
                    Log.i("notification", "New tag found for user ${lastTagsMap[newTag.userId]}: ${newTag.id}")
                }
            }
            val list = lastTagsMap.map { (userId, tags) -> Subscription(userId, tags.joinToString(",")) }
            repository.addSubscriptions(ArrayList(list))
        } catch (e: Exception) {
            Log.i("notification", e.message.toString())
            return Result.failure()
        }
        return Result.success()
    }
}

fun setupWorkManager(context: Context) {
    val TAG = "check_new_tag"
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<UpdateTagsWorker>(
        repeatInterval = 15,
        repeatIntervalTimeUnit = TimeUnit.MINUTES
    ).setConstraints(constraints)
        .addTag(TAG)
        .build()

    WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
    WorkManager.getInstance(context).enqueue(workRequest)
}