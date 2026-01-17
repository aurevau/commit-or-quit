package com.example.commitorquitapp.models

import com.google.firebase.Timestamp

data class User(
    val id: String? = null,
    val fullName: String = "",
    val userName: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now()
) {
    constructor(): this("", "", "", "", null, Timestamp.now())

    val initials: String
        get() {
            if (fullName.isBlank()) return "?"
            return fullName.trim()
                .split("\\s+".toRegex())
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
        }
}