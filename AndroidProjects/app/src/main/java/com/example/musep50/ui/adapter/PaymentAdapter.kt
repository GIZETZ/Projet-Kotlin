
package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.entities.Paiement
import com.example.musep50.databinding.ItemPaymentBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentAdapter : ListAdapter<Paiement, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRANCE)
        private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)

        fun bind(paiement: Paiement) {
            // TODO: Load user name from userId
            binding.payerName.text = "Utilisateur #${paiement.userId}"
            binding.montant.text = "${formatter.format(paiement.montant)} FCFA"
            binding.datePaiement.text = dateFormat.format(paiement.datePaiement)
            binding.methode.text = paiement.methodePaiement

            if (paiement.commentaire.isNullOrBlank()) {
                binding.commentaire.visibility = View.GONE
            } else {
                binding.commentaire.visibility = View.VISIBLE
                binding.commentaire.text = paiement.commentaire
            }
        }
    }
}

class PaymentDiffCallback : DiffUtil.ItemCallback<Paiement>() {
    override fun areItemsTheSame(oldItem: Paiement, newItem: Paiement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Paiement, newItem: Paiement): Boolean {
        return oldItem == newItem
    }
}
