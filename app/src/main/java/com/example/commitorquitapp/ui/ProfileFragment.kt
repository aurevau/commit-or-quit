package com.example.commitorquitapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.commitorquitapp.R
import com.example.commitorquitapp.SettingsFragment
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.databinding.FragmentProfileBinding
import com.example.commitorquitapp.viewmodel.UserViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel





    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = authViewModel.getCurrentUserId() ?: return

        userViewModel.getUserDetailsById(currentUserId) {user ->
            binding.tvUsername.text = user?.userName
            user?.profileImageUrl?.takeIf { it.isNotBlank() }?.let {
                Glide.with(binding.profileImage)
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.profile_icon)
                    .circleCrop()
                    .into(binding.profileImage)
            }
        }

        binding.profileImage.setOnClickListener {
                val intent = Intent(requireActivity(), OnboardingActivity::class.java)
                startActivity(intent)

        }




    }


}