package com.example.commitorquitapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commitorquitapp.R
import com.example.commitorquitapp.SelectUserItem
import com.example.commitorquitapp.SelectUsersAdapter
import com.example.commitorquitapp.UserRecyclerAdapter
import com.example.commitorquitapp.databinding.FragmentSelectUsersBinding
import com.example.commitorquitapp.models.User
import com.example.commitorquitapp.util.REQUEST_SELECT_USERS
import com.example.commitorquitapp.util.RESULT_SELECTED_USER_IDS
import com.example.commitorquitapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch



class SelectUsersFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectUsersBinding

    private lateinit var chipGroup: ChipGroup

    private lateinit var selectUsersAdapter: SelectUsersAdapter

    private lateinit var userViewModel: UserViewModel

    private lateinit var usersRecyclerView: RecyclerView
    private val selectedUserIds = mutableSetOf<String>()
    private var latestUsers: List<User> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        selectUsersAdapter = SelectUsersAdapter { user ->
            toggleSelection(user)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersRecyclerView = binding.rvUsers
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersRecyclerView.adapter = selectUsersAdapter
        chipGroup = binding.chipGroupMembers




        binding.confirmBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_SELECT_USERS,
                Bundle().apply {
                    putStringArrayList(
                        RESULT_SELECTED_USER_IDS,
                        ArrayList(selectedUserIds)
                    )
                }
            )
            dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.users.collect { users ->
                    latestUsers = users
                    buildAndSubmitItems(users)
                    updateSelectedChips(users)
                }
            }
        }




    }



    private fun toggleSelection(user: User) {
        val userId = user.id ?: return

        if (!selectedUserIds.add(userId)) {
            selectedUserIds.remove(userId)
        }
        buildAndSubmitItems(latestUsers)
        updateSelectedChips(latestUsers)
    }

    private fun buildAndSubmitItems(users: List<User>) {
        Log.d("SelectUsersFragment", "buildAndSubmitItems users=${users.size}")

//        val friends = users.filter { it.isFriend }
//        val others = users.filter { !it.isFriend }
        val others = users

        val items = mutableListOf<SelectUserItem>()

//        if (friends.isNotEmpty()) {
//            items += SelectUserItem.Header("Friends")
//            items += friends.map {
//                SelectUserItem.UserItem(
//                    user = it,
//                    isSelected = selectedUserIds.contains(it.id)
//                )
//            }
//        }

        if (others.isNotEmpty()) {
            items += SelectUserItem.Header("Other results")
            items += others.map {
                SelectUserItem.UserItem(
                    user = it,
                    isSelected = selectedUserIds.contains(it.id)
                )
            }
        }

        selectUsersAdapter.submitList(items)
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

    private fun updateSelectedChips(users: List<User>) {
        binding.chipGroupMembers.removeAllViews()

        users
            .filter { selectedUserIds.contains(it.id) }
            .forEach { user ->
                val chip = Chip(requireContext()).apply {
                    text = user.userName ?: user.fullName
                    isCloseIconVisible = true
                    tag = user.id
                    setOnCloseIconClickListener {
                        selectedUserIds.remove(user.id)
                        updateSelectedChips(users)
                        buildAndSubmitItems(users)
                    }
                }
                binding.chipGroupMembers.addView(chip)
            }
    }




}