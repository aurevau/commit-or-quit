package com.example.commitorquitapp.models

import android.net.Uri

enum class MediaType {
    IMAGE,
    VIDEO;

    companion object {
        fun fromUri(uri: Uri): MediaType {
            return if (uri.toString().contains("video")) VIDEO else IMAGE
        }
    }
}