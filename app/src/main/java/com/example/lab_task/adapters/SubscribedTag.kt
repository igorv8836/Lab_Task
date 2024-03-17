package com.example.lab_task.adapters

import com.example.lab_task.model.sqlite.TagEntity

class SubscribedTag(
    tag: TagEntity,
    var isSubscribed: Boolean
) : TagEntity(tag.id, tag.latitude, tag.longitude, tag.description, tag.imagePath,
    tag.likes, tag.isLiked, tag.userId, tag.username) {
}