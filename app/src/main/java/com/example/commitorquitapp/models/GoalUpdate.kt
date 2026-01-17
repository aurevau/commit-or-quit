package com.example.commitorquitapp.models

import com.google.firebase.Timestamp

data class GoalUpdate(
    val id: String = "",
    val goalId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val senderProfilePic: String? = null,
    val media: List<MediaItem> = emptyList(),
    val likes: Map<String, Timestamp> = emptyMap()
) {
}