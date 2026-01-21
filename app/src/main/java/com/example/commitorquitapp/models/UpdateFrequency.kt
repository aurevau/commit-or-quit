package com.example.commitorquitapp.models

enum class UpdateFrequency(val days: Int, val displayName: String) {
    DAILY(1, "Daily"),
    EVERYOTHERDAY(2, "Every other day"),
    WEEKLY(7, "Weekly"),
    MONTHLY(30, "Monthly")
}