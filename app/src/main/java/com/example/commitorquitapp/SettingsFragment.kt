package com.example.commitorquitapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.commitorquitapp.auth.AuthState
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.databinding.FragmentSettingsBinding
import com.example.commitorquitapp.ui.OnboardingActivity
import com.example.commitorquitapp.ui.navigation.AppNavigator
import com.example.commitorquitapp.ui.navigation.Navigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        navigator = AppNavigator(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            authViewModel.onLogout()
        }

        binding.btnBack.setOnClickListener {
            dismiss()
        }






    }


    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog ?: return

//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setWindowAnimations(R.style.DialogSlideInLeftAnimation)

        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return

        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = 0
        behavior.skipCollapsed = true

        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                true
            } else false
        }
    }

    override fun dismiss() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.animate()
            ?.translationX(bottomSheet.width.toFloat())
            ?.setDuration(180)
            ?.withEndAction {
                super.dismiss()
            }
            ?.start()
    }


}