package com.example.commitorquitapp.repository

import android.net.Uri
import android.util.Log
import com.example.commitorquitapp.models.MediaItem
import com.example.commitorquitapp.models.MediaType
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlin.math.sin

object MediaRepository {

    private val storage: FirebaseStorage = Firebase.storage


    fun uploadProfileImageToFirebase(
        imageUri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference.child("profile_images/$userId")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {uri ->
                        onSuccess(uri.toString())
                    }.addOnFailureListener {  exception ->
                        onError(exception.message ?: "failed to get profile picture uri ")
                    }
            }.addOnFailureListener { exception ->
                onError(exception.message ?: "profile picture upload to firebase failed")
            }
    }

    fun uploadMediaFiles(uris: List<Uri>, onComplete: (List<MediaItem>) -> Unit) {
        if (uris.isEmpty()) {
            onComplete(emptyList())
            return
        }

        val mediaItems = mutableListOf<MediaItem>()
        var completed = 0

        uris.forEach { uri ->
            val isVideo = uri.toString().contains("video")
            val type =  if (isVideo) MediaType.VIDEO else MediaType.IMAGE

            val filename = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child("goal_media/$filename")

            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        mediaItems.add(
                            MediaItem(
                            url = downloadUrl.toString(),
                            type = type
                            )
                        )
                        completed++
                        if (completed == uris.size) {
                            onComplete(mediaItems)
                        }
                    }.addOnFailureListener {exception ->
                        Log.e("MediaRepository", "Failed to get downloadUrl", exception)
                        completed++
                        if (completed == uris.size) onComplete(mediaItems)
                    }
                }.addOnFailureListener { exception ->
                    Log.e("MediaRepository", "Failed to upload file", exception)
                    completed++
                    if(completed == uris.size) onComplete(mediaItems)

                }
        }
    }


    private fun uploadFile(
        uri: Uri,
        path: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = storage.reference.child(path)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { onSuccess(it.toString()) }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    fun uploadProfileImage(
        imageUri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        uploadFile(
            uri = imageUri,
            path = "profile_images/$userId",
            onSuccess = onSuccess,
            onError = {onError(it.message ?: "Upload failed")}
        )
    }

    fun uploadGoalMedia(
        uris: List<Uri>,
        onComplete: (List<MediaItem>) -> Unit
    ) {
        if (uris.isEmpty()) {
            onComplete(emptyList())
            return
        }

        val results = mutableListOf<MediaItem>()
        var completed = 0

        uris.forEach { uri ->
            val type = MediaType.fromUri(uri)
            val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"

            uploadFile(
                uri = uri,
                path = "goal_media/$fileName",
                onSuccess = {url ->
                    results.add(MediaItem(url, type))
                    completed++
                    if (completed == uris.size) onComplete(results)
                }, onError = {
                    Log.e("MediaRepository", "Upload failed", it)
                    completed++
                    if (completed == uris.size) onComplete(results)
                }
            )

        }
    }
}