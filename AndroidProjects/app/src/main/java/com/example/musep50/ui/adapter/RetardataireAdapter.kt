package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.entities.User
import com.example.musep50.databinding.ItemRetardataireBinding

class RetardataireAdapter(
    private val onSelectionChanged: (List<User>) -> Unit,
    private val onSendIndividual: (User) -> Unit
) : ListAdapter<User, RetardataireAdapter.RetardataireViewHolder>(RetardataireDiffCallback()) {

    private val selectedUsers = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetardataireViewHolder {
        val binding = ItemRetardataireBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RetardataireViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RetardataireViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun selectAll() {
        selectedUsers.clear()
        currentList.forEach { selectedUsers.add(it.id) }
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    fun clearSelection() {
        selectedUsers.clear()
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        val selected = currentList.filter { selectedUsers.contains(it.id) }
        onSelectionChanged(selected)
    }

    inner class RetardataireViewHolder(
        private val binding: ItemRetardataireBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userName.text = user.nom
            binding.userEmail.text = user.email
            
            if (!user.telephone.isNullOrBlank()) {
                binding.userPhone.visibility = View.VISIBLE
                binding.userPhone.text = "📞 ${user.telephone}"
            } else {
                binding.userPhone.visibility = View.GONE
            }

            binding.checkbox.isChecked = selectedUsers.contains(user.id)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUsers.add(user.id)
                } else {
                    selectedUsers.remove(user.id)
                }
                notifySelectionChanged()
            }

            binding.btnSendIndividual.setOnClickListener {
                onSendIndividual(user)
            }
        }
    }
}

class RetardataireDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
