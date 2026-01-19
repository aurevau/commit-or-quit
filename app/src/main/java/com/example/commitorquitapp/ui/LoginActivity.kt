package com.example.commitorquitapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.commitorquitapp.MainActivity
import com.example.commitorquitapp.R
import com.example.commitorquitapp.auth.AuthState
import com.example.commitorquitapp.databinding.ActivityLoginBinding
import com.example.commitorquitapp.repository.UserRepository
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.ui.navigation.AppNavigator
import com.example.commitorquitapp.ui.navigation.Navigator
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var credentialManager: CredentialManager
    private lateinit var navigator: Navigator
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)




        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        credentialManager = CredentialManager.create(this)
        navigator = AppNavigator(this)
        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnRegister.setOnClickListener {
            register()
        }

        binding.googleLoginBtn.setOnClickListener {
            loginWithGoogle()
        }

        authViewModel.authState.observe(this) {state ->
            when (state) {
                AuthState.LoggedIn -> navigator.toMain()
                AuthState.NeedsOnboarding -> navigator.toOnboarding()
                AuthState.LoggedOut -> Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()

            }
        }



    }


    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        authViewModel.login(email, password, {
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

            },
            onFailure = {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun loginWithGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(baseContext.getString(R.string.default_web_client_id))
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(this@LoginActivity, request)
                handleSignIn(result)
            } catch (exception: GetCredentialException) {
                handleFailure(exception)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        if (result.credential is CustomCredential && result.credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val idToken = googleIdTokenCredential.idToken ?: return

            authViewModel.loginWithGoogle(idToken)

        }
    }

    private fun handleFailure(exception: GetCredentialException) {
        when (exception) {
            is GetCredentialCancellationException -> {
                Toast.makeText(
                    this,
                    "not successfull" + exception.message, Toast.LENGTH_SHORT
                ).show()

            }

            is NoCredentialException -> {

                AlertDialog.Builder(this)
                    .setTitle("Add google account")
                    .setMessage("Please add google account in settings")
                    .setPositiveButton("Yes, go to settings") { dialog, _ ->
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            else -> {
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }




}