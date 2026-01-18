package com.example.commitorquitapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.commitorquitapp.models.User
import com.example.commitorquitapp.repository.UserRepository

class UserViewModel: ViewModel() {

    fun getUserDetailsById(userId: String, callback: (User?) -> Unit) {
        UserRepository.getUserDetailsById(userId, callback)
    }
}