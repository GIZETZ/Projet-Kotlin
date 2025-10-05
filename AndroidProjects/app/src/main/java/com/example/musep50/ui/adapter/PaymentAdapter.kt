package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.data.dao.PaiementWithUser
import com.example.musep50.databinding.ItemPaymentBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentAdapter : ListAdapter<PaiementWithUser, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

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

        fun bind(paiementWithUser: PaiementWithUser) {
            binding.payerName.text = paiementWithUser.payerName
            binding.montant.text = "${formatter.format(paiementWithUser.paiement.montant)} FCFA"
            binding.datePaiement.text = dateFormat.format(paiementWithUser.paiement.datePaiement)
            binding.methode.text = paiementWithUser.paiement.methodePaiement

            if (paiementWithUser.paiement.commentaire.isNullOrBlank()) {
                binding.commentaire.visibility = View.GONE
            } else {
                binding.commentaire.visibility = View.VISIBLE
                binding.commentaire.text = paiementWithUser.paiement.commentaire
            }
        }
    }
}

class PaymentDiffCallback : DiffUtil.ItemCallback<PaiementWithUser>() {
    override fun areItemsTheSame(oldItem: PaiementWithUser, newItem: PaiementWithUser): Boolean {
        return oldItem.paiement.id == newItem.paiement.id
    }

    override fun areContentsTheSame(oldItem: PaiementWithUser, newItem: PaiementWithUser): Boolean {
        return oldItem == newItem
    }
}
