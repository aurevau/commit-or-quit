package com.example.commitorquitapp.repository

import com.example.commitorquitapp.models.Goal
import com.example.commitorquitapp.models.GoalUpdate
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object GoalRepository {

    private val db = Firebase.firestore


    suspend fun createGoal(goal: Goal): Result<String> {
        return try {
            val ref = db.collection("goals").document()
            val goalWithId = goal.copy(goalId = ref.id)

            ref.set(goalWithId).await()
            Result.success(ref.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }

    }



}