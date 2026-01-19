package com.example.commitorquitapp.repository

import com.example.commitorquitapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

object UserRepository {
    private val auth = FirebaseAuth.getInstance()


    private val db = Firebase.firestore

    fun saveUserToFirestore(
        fullName: String,
        userName: String?,
        bio: String?,
        email: String,
        profileImageUrl: String?,
        createdAt: Timestamp,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit) {

        val userId = auth.currentUser?.uid
            ?: return onFailure("User not logged in")

        val userData =
            User(
                id = userId,
                fullName = fullName,
                userName = userName,
                bio = bio,
                email = email,
                profileImageUrl = profileImageUrl,
                createdAt = createdAt
            )


            db.collection("users")
                .document(userId)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { exception ->
                    onFailure(exception.message ?: "failed to save user to database")
                }


    }

    fun getUserDetailsById(userId: String, callback: (User?) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user?.copy(id = document.id))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }


}