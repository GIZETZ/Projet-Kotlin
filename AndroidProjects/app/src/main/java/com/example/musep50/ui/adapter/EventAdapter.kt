package com.example.musep50.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musep50.R
import com.example.musep50.data.entities.Event
import com.example.musep50.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onEditClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.eventName.text = event.nom
            binding.eventDescription.text = event.description ?: "Aucune description"
            binding.statusChip.text = event.statut

            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            binding.eventStartDate.text = dateFormatter.format(Date(event.dateDebut))
            
            binding.operationCount.text = "0"

            binding.root.setOnClickListener {
                onItemClick(event)
            }

            binding.btnEventMenu.setOnClickListener {
                showPopupMenu(it, event)
            }
        }

        private fun showPopupMenu(view: android.view.View, event: Event) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.event_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_event -> {
                        onEditClick(event)
                        true
                    }
                    R.id.action_delete_event -> {
                        onDeleteClick(event)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}
