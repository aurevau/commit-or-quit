package com.example.commitorquitapp.ui.navigation

import android.app.Activity
import android.content.Intent
import com.example.commitorquitapp.MainActivity
import com.example.commitorquitapp.ui.LoginActivity
import com.example.commitorquitapp.ui.OnboardingActivity

class AppNavigator(private val activity: Activity): Navigator {
    override fun toMain() {
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity.startActivity(intent)

    }

    override fun toLogin() {
       activity.startActivity(Intent(activity, LoginActivity::class.java))
    }

    override fun toOnboarding() {
        activity.startActivity(Intent(activity, OnboardingActivity::class.java))
    }

    override fun back() {
       activity.finish()
    }
}