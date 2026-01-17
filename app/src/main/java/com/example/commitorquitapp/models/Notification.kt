package com.example.commitorquitapp.models

import com.google.firebase.Timestamp

data class Notification(
    val id: String = "",
    val type: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String? = null,
    val createdAt: Timestamp? = null,
    val isRead: Boolean = false,
    val relatedId: String = "",
    val relatedTitle: String? = null,
    val senderProfilePic: String? = null,
    val mediaUrl: String? = null,
    val mediaType: String? = null,

    )
