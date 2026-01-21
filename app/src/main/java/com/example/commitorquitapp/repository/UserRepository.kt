package com.example.commitorquitapp.repository

import android.util.Log
import com.example.commitorquitapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow


object UserRepository {
    private val auth = FirebaseAuth.getInstance()


    private val db = Firebase.firestore

    fun observeUsers(): Flow<List<User>> = callbackFlow {
        val listener: ListenerRegistration =
            db.collection("users")
                .addSnapshotListener { snapshot, error ->
                    Log.d("UserRepo", "snapshot size=${snapshot?.size()}")

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val users = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(User::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    trySend(users).isSuccess
                }
        awaitClose { listener.remove() }
    }

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