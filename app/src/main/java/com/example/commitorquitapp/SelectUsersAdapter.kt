package com.example.commitorquitapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commitorquitapp.databinding.ListItemSelectedUsersBinding
import com.example.commitorquitapp.models.User

class SelectUsersAdapter(private val onUserClick: (User) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<SelectUserItem>()

    fun submitList(newItems: List<SelectUserItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is SelectUserItem.Header -> VIEW_TYPE_HEADER
            is SelectUserItem.UserItem -> VIEW_TYPE_USER
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(
                    R.layout.item_section_header,
                    parent,
                    false
                )
                HeaderViewHolder(view)
            }

            else -> {
                val binding = ListItemSelectedUsersBinding.inflate(
                    inflater,
                    parent,
                    false
                )
                UserViewHolder(binding, onUserClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SelectUserItem.Header ->
                (holder as HeaderViewHolder).bind(item.title)

            is SelectUserItem.UserItem ->
                (holder as UserViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_USER = 1
    }

    inner class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val title: TextView = view.findViewById(R.id.tvSectionTitle)

        fun bind(text: String) {
            title.text = text
        }
    }

    inner class UserViewHolder(
        val binding: ListItemSelectedUsersBinding,
        private val onItemClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectUserItem.UserItem) {
            val user = item.user

            binding.tvUsername.text = user.userName

            if (!user.profileImageUrl.isNullOrEmpty()) {
                binding.tvInitials.visibility = View.GONE
                binding.ivProfilePic.visibility = View.VISIBLE

                Glide.with(binding.ivProfilePic)
                    .load(user.profileImageUrl)
                    .circleCrop()
                    .into(binding.ivProfilePic)
            } else {
                binding.ivProfilePic.visibility = View.GONE
                binding.tvInitials.visibility = View.VISIBLE
                binding.tvInitials.text = user.initials
            }

            binding.root.isSelected = item.isSelected

            binding.root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

}
