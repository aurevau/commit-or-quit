package com.example.commitorquitapp.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

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
}