package com.example.commitorquitapp.repository

import com.example.commitorquitapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object UserRepository {

    private val db = Firebase.firestore



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