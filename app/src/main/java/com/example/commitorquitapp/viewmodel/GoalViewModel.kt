package com.example.commitorquitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commitorquitapp.CreateGoalState
import com.example.commitorquitapp.models.Goal
import com.example.commitorquitapp.models.GoalUpdate
import com.example.commitorquitapp.models.Invitation
import com.example.commitorquitapp.models.Privacy
import com.example.commitorquitapp.repository.GoalRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoalViewModel: ViewModel() {

    private val _createGoalState = MutableStateFlow<CreateGoalState>(CreateGoalState.Idle)
    val createGoalState: StateFlow<CreateGoalState> = _createGoalState


    fun createGoal(
        goalTitle: String,
        goalDescription: String,
        creatorId: String,
        goalCategory: String,
        updateFrequency: Int = 0,
        memberIds: List<String>,
        startDate: Timestamp? = null,
        endDate: Timestamp? = null,
        firstUpdate: GoalUpdate? = null,
        privacy: Privacy = Privacy.MEMBERS
    ) {
        val invitations = memberIds
            .filter { it != creatorId }
            .associateWith {
                Invitation(invitedAt = Timestamp.now())
            }

        val goal = Goal(
            goalTitle = goalTitle,
            goalDescription = goalDescription,
            creatorId = creatorId,
            goalCategory = goalCategory,
            updateFrequency = updateFrequency,
            memberIds = memberIds,
            progress = 0,
            startDate = startDate,
            endDate = endDate,
            lastUpdate = firstUpdate,
            invitations = invitations,
            privacy = privacy

        )

        viewModelScope.launch {
            _createGoalState.value = CreateGoalState.Loading
            GoalRepository.createGoal(goal)
                .onSuccess { goalId ->
                    _createGoalState.value =
                        CreateGoalState.Success(goalId)
                }
                .onFailure { throwable ->
                    _createGoalState.value =
                        CreateGoalState.Error(throwable)
                }
        }


    }

    fun resetCreateGoalState() {
        _createGoalState.value = CreateGoalState.Idle
    }
}