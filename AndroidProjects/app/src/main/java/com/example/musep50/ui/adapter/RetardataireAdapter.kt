package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.entities.Payer
import com.example.musep50.databinding.ItemRetardataireBinding

class RetardataireAdapter(
    private val onSelectionChanged: (List<Payer>) -> Unit,
    private val onSendIndividual: (Payer) -> Unit
) : ListAdapter<Payer, RetardataireAdapter.RetardataireViewHolder>(RetardataireDiffCallback()) {

    private val selectedPayers = mutableSetOf<Long>()

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
        selectedPayers.clear()
        currentList.forEach { selectedPayers.add(it.id) }
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    fun clearSelection() {
        selectedPayers.clear()
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        val selected = currentList.filter { selectedPayers.contains(it.id) }
        onSelectionChanged(selected)
    }

    inner class RetardataireViewHolder(
        private val binding: ItemRetardataireBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payer: Payer) {
            binding.userName.text = payer.nom
            binding.userEmail.text = if (!payer.contact.isNullOrBlank()) {
                "+225${payer.contact}"
            } else {
                "Pas de contact"
            }
            binding.checkbox.isChecked = selectedPayers.contains(payer.id)

            binding.root.setOnClickListener {
                if (selectedPayers.contains(payer.id)) {
                    selectedPayers.remove(payer.id)
                } else {
                    selectedPayers.add(payer.id)
                }
                binding.checkbox.isChecked = selectedPayers.contains(payer.id)
                notifySelectionChanged()
            }

            binding.btnSendIndividual.setOnClickListener {
                onSendIndividual(payer)
            }
        }
    }
}

class RetardataireDiffCallback : DiffUtil.ItemCallback<Payer>() {
    override fun areItemsTheSame(oldItem: Payer, newItem: Payer): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Payer, newItem: Payer): Boolean {
        return oldItem == newItem
    }
}