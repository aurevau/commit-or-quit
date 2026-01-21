package com.example.commitorquitapp

sealed class CreateGoalState {
    object Idle: CreateGoalState()
    object Loading: CreateGoalState()
    data class Success(val goalId: String): CreateGoalState()
    data class Error(val throwable: Throwable): CreateGoalState()
}