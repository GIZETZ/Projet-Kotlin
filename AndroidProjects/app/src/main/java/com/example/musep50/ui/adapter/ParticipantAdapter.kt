package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.entities.Payer
import com.example.musep50.databinding.ItemParticipantBinding

class ParticipantAdapter(
    private val onDeleteClick: (Payer) -> Unit
) : ListAdapter<Payer, ParticipantAdapter.ParticipantViewHolder>(ParticipantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding = ItemParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ParticipantViewHolder(
        private val binding: ItemParticipantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payer: Payer) {
            binding.participantName.text = payer.nom
            
            if (payer.contact.isNullOrBlank()) {
                binding.participantContact.visibility = View.GONE
            } else {
                binding.participantContact.visibility = View.VISIBLE
                binding.participantContact.text = payer.contact
            }

            if (payer.montantPersonnalise != null) {
                binding.participantMontant.visibility = View.VISIBLE
                val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.FRANCE)
                binding.participantMontant.text = "Montant personnalisé: ${formatter.format(payer.montantPersonnalise)} FCFA"
            } else {
                binding.participantMontant.visibility = View.GONE
            }

            binding.btnDeleteParticipant.setOnClickListener {
                onDeleteClick(payer)
            }
        }
    }
}

class ParticipantDiffCallback : DiffUtil.ItemCallback<Payer>() {
    override fun areItemsTheSame(oldItem: Payer, newItem: Payer): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Payer, newItem: Payer): Boolean {
        return oldItem == newItem
    }
}
