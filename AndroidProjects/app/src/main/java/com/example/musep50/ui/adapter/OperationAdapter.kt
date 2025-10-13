package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.R
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ItemOperationBinding
import com.example.musep50.viewmodel.OperationStats
import java.text.NumberFormat
import java.util.Locale

class OperationAdapter(
    private val onItemClick: (Operation) -> Unit,
    private val onEditClick: (Operation) -> Unit,
    private val onDeleteClick: (Operation) -> Unit
) : ListAdapter<Operation, OperationAdapter.OperationViewHolder>(OperationDiffCallback()) {
    
    private var operationStats = mapOf<Long, OperationStats>()
    
    fun setOperationStats(stats: Map<Long, OperationStats>) {
        operationStats = stats
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding = ItemOperationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OperationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        holder.bind(getItem(position), operationStats[getItem(position).id])
    }
    
    inner class OperationViewHolder(
        private val binding: ItemOperationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(operation: Operation, stats: OperationStats?) {
            binding.operationName.text = operation.nom
            binding.operationType.text = operation.type
            binding.statusChip.text = operation.statut
            
            stats?.let {
                val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)
                binding.montantCollecte.text = "${formatter.format(it.montantCollecte)} FCFA"
                binding.montantRestant.text = "${formatter.format(it.montantRestant)} FCFA"
                binding.nombrePayeurs.text = it.nombrePaiements.toString()
                binding.progressBar.progress = it.pourcentage
            } ?: run {
                binding.montantCollecte.text = "0 FCFA"
                binding.montantRestant.text = "${NumberFormat.getNumberInstance(Locale.FRANCE).format(operation.montantCible)} FCFA"
                binding.nombrePayeurs.text = "0"
                binding.progressBar.progress = 0
            }
            
            binding.root.setOnClickListener {
                onItemClick(operation)
            }

            binding.btnOperationMenu.setOnClickListener {
                showPopupMenu(it, operation)
            }
        }

        private fun showPopupMenu(view: android.view.View, operation: Operation) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.operation_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_operation -> {
                        onEditClick(operation)
                        true
                    }
                    R.id.action_delete_operation -> {
                        onDeleteClick(operation)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}

class OperationDiffCallback : DiffUtil.ItemCallback<Operation>() {
    override fun areItemsTheSame(oldItem: Operation, newItem: Operation): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Operation, newItem: Operation): Boolean {
        return oldItem == newItem
    }
}
