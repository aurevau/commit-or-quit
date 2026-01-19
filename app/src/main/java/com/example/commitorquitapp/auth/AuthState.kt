package com.example.commitorquitapp.auth

sealed class AuthState {
    object LoggedOut: AuthState()
    object LoggedIn: AuthState()
    object NeedsOnboarding: AuthState()
}