package com.example.commitorquitapp

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.commitorquitapp.databinding.ActivityMainBinding
import com.example.commitorquitapp.ui.CreateGoalFragment
import com.example.commitorquitapp.ui.NotificationFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom nav
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
    }



}