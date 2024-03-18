package com.example.lab_task.model.other

import android.util.Log
import com.example.lab_task.App
import com.example.lab_task.model.repository.TagRepository
import com.example.lab_task.model.sqlite.Subscription
import com.example.lab_task.notification.NotificationUtils
import kotlinx.coroutines.flow.first

object ChangeChecker {
    suspend fun check(repository: TagRepository, withNotification: Boolean = true) {
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
                    if (withNotification) {
                        NotificationUtils.showNotification(
                            App.instance,
                            "Новая метка",
                            "Пользователь ${newTag.username} опубликовал новую метку"
                        )
                    }
                    Log.i("notification", "New tag found for user ${lastTagsMap[newTag.userId]}: ${newTag.id}")
                }
            }
            val list = lastTagsMap.map { (userId, tags) -> Subscription(userId, tags.joinToString(",")) }
            repository.addSubscriptions(ArrayList(list))
        } catch (e: Exception) { Log.i("notification", e.message.toString()) }
    }
}