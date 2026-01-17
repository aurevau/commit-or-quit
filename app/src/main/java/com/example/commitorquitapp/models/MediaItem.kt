package com.example.commitorquitapp.models

data class MediaItem(
    val url: String = "",
    val type: MediaType = MediaType.IMAGE
)

enum class MediaType {
    IMAGE,
    VIDEO
}