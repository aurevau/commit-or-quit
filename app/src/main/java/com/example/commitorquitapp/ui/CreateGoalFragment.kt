package com.example.commitorquitapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.commitorquitapp.CreateGoalState
import com.example.commitorquitapp.R
import com.example.commitorquitapp.auth.AuthViewModel
import com.example.commitorquitapp.databinding.FragmentCreateGoalBinding
import com.example.commitorquitapp.models.Privacy
import com.example.commitorquitapp.models.UpdateFrequency
import com.example.commitorquitapp.util.REQUEST_SELECT_USERS
import com.example.commitorquitapp.util.RESULT_SELECTED_USER_IDS
import com.example.commitorquitapp.viewmodel.GoalViewModel
import com.example.commitorquitapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreateGoalFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCreateGoalBinding
    private lateinit var goalViewModel: GoalViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var currentUserId: String

    private var startDate: Timestamp? = null
    private var endDate: Timestamp? = null
    private var privacy: Privacy = Privacy.MEMBERS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalViewModel = ViewModelProvider(requireActivity())[GoalViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = authViewModel.getCurrentUserId() ?: return
        setupUpdateFrequencySpinner()
        parentFragmentManager.setFragmentResultListener(
            REQUEST_SELECT_USERS,
            viewLifecycleOwner
        ) {_, bundle ->
            val userIds = bundle.getStringArrayList(RESULT_SELECTED_USER_IDS) ?: emptyList()

            showSelectedUsersAsChip(userIds)
        }

        binding.etStartDate.setOnClickListener {
            datePicker(true)
        }

        binding.etEndDate.setOnClickListener {
            datePicker(false)
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                goalViewModel.createGoalState.collect { state ->
                    when (state) {
                        is CreateGoalState.Idle -> {

                        }
                        is CreateGoalState.Loading -> {
//                            showLoading(true)
                        }
                        is CreateGoalState.Success -> {
//                            showLoading(false)
//                            navigateToGoal(state.goalId)
                            goalViewModel.resetCreateGoalState()
                        }
                        is CreateGoalState.Error -> {
//                            showLoading(false)
//                            showError(state.throwable.message)
                        }
                    }
                }
            }
        }



        binding.privacyToggleRow1.addOnButtonCheckedListener {_, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            privacy = when (checkedId) {
                R.id.btn_members_only -> Privacy.MEMBERS
                R.id.btn_everyone -> Privacy.PUBLIC
                else -> Privacy.MEMBERS
            }
        }

        binding.btnPost.setOnClickListener {
            val memberIds = getSelectedMemberIds()
                .plus(currentUserId)
                .distinct()

            goalViewModel.createGoal(
                goalTitle = binding.etGoalTitle.text.toString(),
                goalDescription =  binding.etGoalDescription.text.toString(),
                creatorId = currentUserId,
                startDate = startDate,
                endDate = endDate,
                goalCategory = getSelectedCategory(),
                updateFrequency = getUpdateFrequency(),
                memberIds = memberIds,
                privacy = privacy
            )
            dismiss()
        }

        binding.cardAddFriends.setOnClickListener {
            val dialog = SelectUsersFragment()
            dialog.show(parentFragmentManager, "select_users_fragment_dialog")
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.actvUpdateFrequency.setOnClickListener {
            binding.actvUpdateFrequency.showDropDown()
        }

        binding.actvUpdateFrequency.setText(
            "Choose update frequency",
            false
        )
    }



    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog ?: return

        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return

        val displayMetrics = resources.displayMetrics
        bottomSheet.layoutParams.height =
            (displayMetrics.heightPixels * 0.95).toInt()

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isDraggable = true
    }

    private fun showSelectedUsersAsChip(userIds: List<String>) {
        binding.chipGroupFriends.removeAllViews()

        userIds.forEach { userId ->
            userViewModel.getUserDetailsById(userId) {user ->
                val chip = Chip(requireContext()).apply {
                    text = user?.userName
                    isCheckable = true
                    isChecked = true
                    tag = userId
                }
                binding.chipGroupFriends.addView(chip)
            }

        }
    }

    private fun getSelectedMemberIds(): List<String> {
        return binding.chipGroupFriends.checkedChipIds.mapNotNull { chipId ->
            val chip = binding.chipGroupFriends.findViewById<Chip>(chipId)
            chip.tag as? String
        }
    }

    private fun getSelectedCategory(): String {
        val checkedId = binding.chipGroupCategory.checkedChipId
        return if (checkedId != View.NO_ID) {
            binding.chipGroupCategory
                .findViewById<Chip>(checkedId)
                .text
                .toString()
        } else ""
    }

    private fun getUpdateFrequency(): Int {
        val selected = binding.actvUpdateFrequency.text.toString()

        return UpdateFrequency.values()
            .firstOrNull { it.displayName == selected }
            ?.days ?: 0
    }



    private fun datePicker(isStartDate: Boolean) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(if (isStartDate) "Select Start Date" else "Select End Date")
            .build()

        picker.show(childFragmentManager, "DATE_PICKER")

        picker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val timestamp = Timestamp(date)
            if (isStartDate) {
                startDate = timestamp
                binding.etStartDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
            } else {
                endDate = timestamp
                binding.etEndDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
            }
        }


    }

    private fun setupUpdateFrequencySpinner() {
        val options = UpdateFrequency.values().map { it.displayName }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            options
        )

        binding.actvUpdateFrequency.setAdapter(adapter)
    }
}