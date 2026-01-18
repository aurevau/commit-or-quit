package com.example.commitorquitapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.commitorquitapp.MainActivity
import com.example.commitorquitapp.R
import com.example.commitorquitapp.databinding.ActivityLoginBinding
import com.example.commitorquitapp.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)




        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnRegister.setOnClickListener {
            register()
        }



    }


    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        authViewModel.login(email, password, {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, {exception ->
            Toast.makeText(this, "Failed to login: ${exception.message}", Toast.LENGTH_LONG).show()
        })
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Cannot register without email"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Cannot register without password"
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Please enter at least 6 characters"
            return
        }

        authViewModel.register(
            email = email,
            password = password,
            onSuccess = {
                // navigera vidare
            },
            onFailure = {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        )
    }

}