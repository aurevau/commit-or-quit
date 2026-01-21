package com.example.commitorquitapp.models

import com.google.firebase.Timestamp

data class Invitation(
    val status: InvitationStatus = InvitationStatus.INVITED,
    val invitedAt: Timestamp? = null,
    val resondedAt: Timestamp? = null
) {
}