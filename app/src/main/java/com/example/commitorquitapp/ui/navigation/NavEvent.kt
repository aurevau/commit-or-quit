package com.example.commitorquitapp.ui.navigation

sealed class NavEvent {
    object ToMain: NavEvent()
    object ToLogin: NavEvent()
    object ToOnboarding: NavEvent()
    object Back: NavEvent()
    object OpenSettings: NavEvent()
    object ShowDetails: NavEvent()
}