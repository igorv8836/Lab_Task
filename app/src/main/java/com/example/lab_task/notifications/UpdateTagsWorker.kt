package com.example.lab_task.notifications

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.TagEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import java.util.concurrent.TimeUnit

class UpdateTagsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val repository = TagRepository

    override suspend fun doWork(): Result {
        try {
            val newTags = repository.getTags().first()
            val subs = repository.getSubscriptions()

            val lastTagsMap = mutableMapOf<String, List<TagEntity>>()

            subs.forEach { subscription ->
                val lastTags = subscription.last_tags?.split(",")?.mapNotNull { tagId ->
                    repository.getTag(tagId)
                }
                if (lastTags != null) {
                    lastTagsMap[subscription.user_id] = lastTags
                }
            }

            newTags.forEach { newTag ->
                subs.forEach { subscription ->
                    val lastTags = lastTagsMap[subscription.user_id]
                    if (lastTags != null && newTag !in lastTags) {
                        Log.i("Notification", "New tag found for user ${subscription.user_id}: ${newTag.id}")
                    }
                }
            }
            //NotificationUtils.showNotification(applicationContext, "Title", "Message")
        } catch (e: Exception) {
            Log.i("notification", e.message.toString())
            return Result.failure()
        }
        return Result.success()
    }
}

fun setupWorkManager(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<UpdateTagsWorker>(
        repeatInterval = 24,
        repeatIntervalTimeUnit = TimeUnit.SECONDS
    ).setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)

}