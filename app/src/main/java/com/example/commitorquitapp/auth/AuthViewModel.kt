package com.example.commitorquitapp.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commitorquitapp.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    private val auth = Firebase.auth

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess()
            checkOnboarding(it.user?.uid)}
            .addOnFailureListener {
                Log.e("AuthViewModel", "Login failed", it)
                onFailure(it) }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess()
            checkOnboarding(it.user?.uid)}
            .addOnFailureListener { onFailure(it)
                _authState.value = AuthState.LoggedOut}
    }

    fun loginWithGoogle(
        idToken: String
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
            checkOnboarding(it.user?.uid)}
            .addOnFailureListener {
                _authState.value = AuthState.LoggedOut
            }
    }

    private fun checkOnboarding(userId: String?) {
        userId ?: return
        UserRepository.getUserDetailsById(userId) {user ->
            if (user?.fullName.isNullOrEmpty()) {
                _authState.postValue(AuthState.NeedsOnboarding)
            } else {
                _authState.postValue(AuthState.LoggedIn)
            }
        }
    }


    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun onLoginSuccess(needsOnboarding: Boolean) {
        _authState.value =
            if (needsOnboarding) AuthState.NeedsOnboarding
            else AuthState.LoggedIn
    }

    fun onLogout() {
        auth.signOut()
        _authState.value = AuthState.LoggedOut
    }


}