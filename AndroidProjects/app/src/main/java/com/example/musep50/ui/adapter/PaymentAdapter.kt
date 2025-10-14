package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.dao.PaiementWithPayer
import com.example.musep50.databinding.ItemPaymentBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentAdapter(
    private val onPaymentClick: (com.example.musep50.data.dao.PaiementWithPayer) -> Unit
) : ListAdapter<com.example.musep50.data.dao.PaiementWithPayer, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding, onPaymentClick)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentViewHolder(
        private val binding: ItemPaymentBinding,
        private val onPaymentClick: (com.example.musep50.data.dao.PaiementWithPayer) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRANCE)
        private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)

        fun bind(paiementWithPayer: com.example.musep50.data.dao.PaiementWithPayer) {
            binding.payerName.text = paiementWithPayer.payerName
            binding.montant.text = "${formatter.format(paiementWithPayer.paiement.montant)} FCFA"
            binding.datePaiement.text = dateFormat.format(paiementWithPayer.paiement.datePaiement)
            binding.methode.text = paiementWithPayer.paiement.methodePaiement
            
            binding.root.setOnClickListener {
                onPaymentClick(paiementWithPayer)
            }

            if (paiementWithPayer.paiement.commentaire.isNullOrBlank()) {
                binding.commentaire.visibility = View.GONE
            } else {
                binding.commentaire.visibility = View.VISIBLE
                binding.commentaire.text = paiementWithPayer.paiement.commentaire
            }
        }
    }
}

class PaymentDiffCallback : DiffUtil.ItemCallback<com.example.musep50.data.dao.PaiementWithPayer>() {
    override fun areItemsTheSame(oldItem: com.example.musep50.data.dao.PaiementWithPayer, newItem: com.example.musep50.data.dao.PaiementWithPayer): Boolean {
        return oldItem.paiement.id == newItem.paiement.id
    }

    override fun areContentsTheSame(oldItem: com.example.musep50.data.dao.PaiementWithPayer, newItem: com.example.musep50.data.dao.PaiementWithPayer): Boolean {
        return oldItem == newItem
    }
}