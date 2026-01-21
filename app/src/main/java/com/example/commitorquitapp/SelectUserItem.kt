package com.example.commitorquitapp

import com.example.commitorquitapp.models.User

sealed class SelectUserItem {

    data class Header(
        val title: String
    ): SelectUserItem()

    data class UserItem(
        val user: User,
        val isSelected: Boolean
    ): SelectUserItem()
}