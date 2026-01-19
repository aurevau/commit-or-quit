package com.example.commitorquitapp.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.commitorquitapp.MainActivity
import com.example.commitorquitapp.R
import com.example.commitorquitapp.databinding.ActivityOnboardingBinding
import com.example.commitorquitapp.repository.MediaRepository
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.viewmodel.UserViewModel
import com.google.firebase.Timestamp

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var userViewModel: UserViewModel

    private var imageUri: Uri? = null
    private var currentUserId: String? = null


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
        uri?.let {
            Log.d("SOUT", it.toString())
            imageUri = it
            binding.profileImage.setImageURI(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        currentUserId = authViewModel.getCurrentUserId() ?: return

        if (currentUserId != null) {
            userViewModel.getUserDetailsById(currentUserId!!) {user ->
                if (user?.fullName?.isBlank() == true || user?.userName?.isBlank() == true) {
                    binding.btnCancel.visibility = View.GONE
                } else {
                    binding.btnCancel.visibility = View.VISIBLE
                }
            }
        }

        binding.btnAddProfilePicture.setOnClickListener {
            chooseImage()
        }
        binding.btnSave.setOnClickListener {
            saveUser()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

    }


    private fun saveUser() {
        val newUsername = binding.etUsername.text.toString()
        val newFullName = binding.etFullName.text.toString()
        val newBio = binding.etBio.text.toString()
        val email = authViewModel.getCurrentUserEmail() ?: return

        if (newFullName.length < 6) {
            binding.etFullName.error = "Please write your full name"
            return
        }

        if (newUsername.length < 3) {
            binding.etUsername.error = "Username must be longer than 3 charactes"
            return
        }

        userViewModel.getUserDetailsById(currentUserId!!) { user ->
            if (imageUri == null) {
                saveUserData(
                    newFullName,
                    newUsername,
                    newBio,
                    email,
                    user?.profileImageUrl
                )
            } else {
                uploadImageAndSave(currentUserId!!, newFullName, newUsername, newBio, email)
            }
        }
    }


    private fun uploadImageAndSave(
        userId: String,
        fullName: String,
        userName: String,
        bio: String,
        email: String) {
        if (currentUserId != null) {
            MediaRepository.uploadProfileImageToFirebase(
                imageUri = imageUri!!,
                userId = currentUserId!!,
                onSuccess = { downloadUrl ->
                    saveUserData(fullName, userName, bio, email, downloadUrl)
                }, onError = {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun saveUserData(
        fullName: String,
        userName: String,
        bio: String,
        email : String,
        imageUrl: String?) {
        userViewModel.saveUserToFirestore(
            fullName = fullName,
            userName = userName,
            bio = bio,
            email = email,
            profileImageUrl = imageUrl,
            createdAt = Timestamp.now(),
            onSuccess = {goToMainActivity()},
            onFailure = {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()

            }
        )
    }



    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun chooseImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun setData() {
        currentUserId?.let { id ->
            userViewModel.getUserDetailsById(id) { user ->
                user?.apply {
                    fullName?.takeIf { it.isNotBlank() }?.let(binding.etFullName::setText)
                    userName?.takeIf { it.isNotBlank() }?.let(binding.etUsername::setText)
                    bio?.takeIf { it.isNotBlank() }?.let(binding.etBio::setText)
                    profileImageUrl?.takeIf { it.isNotBlank() }?.let {
                        Glide.with(binding.profileImage)
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.profile_icon)
                            .circleCrop()
                            .into(binding.profileImage)
                    }
                }
            }
        }
    }
}