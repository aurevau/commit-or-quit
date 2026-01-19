package com.example.commitorquitapp

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.commitorquitapp.auth.AuthState
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.databinding.ActivityMainBinding
import com.example.commitorquitapp.ui.CreateGoalFragment
import com.example.commitorquitapp.ui.NotificationFragment
import com.example.commitorquitapp.ui.navigation.AppNavigator
import com.example.commitorquitapp.ui.navigation.Navigator


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigator: Navigator
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        navigator = AppNavigator(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController


        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.menu
            .findItem(R.id.createGoalFragment)
            .setOnMenuItemClickListener {
                CreateGoalFragment()
                    .show(supportFragmentManager, "CreateGoalBottomSheet")
                true
            }


        binding.btnNotification.setOnClickListener {
            val dialog = NotificationFragment()
            dialog.show(supportFragmentManager, "notifications_fragment_dialog")
        }

        authViewModel.authState.observe(this) {state ->
            when (state) {
                AuthState.LoggedIn -> {

                }
                AuthState.NeedsOnboarding -> {
                    navigator.toOnboarding()
                }

                AuthState.LoggedOut ->
                    navigator.toLogin()
            }
        }
    }



}