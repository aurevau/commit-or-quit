package com.example.commitorquitapp.models

data class Goal(
    val goalId: String = "",
    val goalTitle: String = "",
    val goalDescription: String = "",
    val creatorId: String = "",
    val memberIds: List<String> = emptyList(),
    val lastUpdate: GoalUpdate? = null,
    val progress: Int = 0,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,

    val invitedAt: Map<String, Timestamp> = emptyMap(),
    val seenAt: Map<String, Timestamp> = emptyMap(),
    val joinedAt: Map<String, Timestamp> = emptyMap(),
    val declinedAt: Map<String, Timestamp> = emptyMap(),
) {
}