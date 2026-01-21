package com.example.commitorquitapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commitorquitapp.models.User
import com.example.commitorquitapp.repository.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        observeUsers()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            UserRepository.observeUsers().catch { error ->

            }.collect { userList ->
                Log.d("UserVM", "users=${userList.size}")

                _users.value = userList
            }
        }
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
        UserRepository.saveUserToFirestore(fullName, userName, bio, email, profileImageUrl, createdAt, onSuccess, onFailure)
    }

    fun getUserDetailsById(userId: String, callback: (User?) -> Unit) {
        UserRepository.getUserDetailsById(userId, callback)
    }
}