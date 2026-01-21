package com.example.commitorquitapp.models

import com.google.firebase.Timestamp

data class Goal(
    val goalId: String = "",
    val goalCategory: String = "",
    val goalTitle: String = "",
    val goalDescription: String = "",
    val creatorId: String = "",
    val memberIds: List<String> = emptyList(),
    val invitations: Map<String, Invitation> = emptyMap(),
    val lastUpdate: GoalUpdate? = null,
    val progress: Int = 0,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val updateFrequency: Int = 0,
    val media: List<MediaItem> = emptyList(),

) {
}