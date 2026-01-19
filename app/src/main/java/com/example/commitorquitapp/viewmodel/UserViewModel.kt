package com.example.commitorquitapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.commitorquitapp.models.User
import com.example.commitorquitapp.repository.UserRepository
import com.google.firebase.Timestamp

class UserViewModel: ViewModel() {

    fun saveUserToFirestore(
        fullName: String,
        userName: String?,
        bio: String?,
        email: String,
        profileImageUrl: String?,
        createdAt: Timestamp,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit) {
        UserRepository.saveUserToFirestore(fullName, userName, bio, email, profileImageUrl, createdAt, onSuccess, onFailure)
    }

    fun getUserDetailsById(userId: String, callback: (User?) -> Unit) {
        UserRepository.getUserDetailsById(userId, callback)
    }
}