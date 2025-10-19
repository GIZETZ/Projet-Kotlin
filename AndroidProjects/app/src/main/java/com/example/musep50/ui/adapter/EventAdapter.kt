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
    private val onDeleteClick: (Event) -> Unit,
    private val onAddImageClick: (Event) -> Unit,
    private val onRemoveImageClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    private var operationCounts: Map<Long, Int> = emptyMap()

    fun setOperationCounts(counts: Map<Long, Int>) {
        this.operationCounts = counts
        notifyDataSetChanged()
    }

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
            
            val count = operationCounts[event.id] ?: 0
            binding.operationCount.text = count.toString()

            // Optional cover image
            val imageView = binding.eventImage
            val uri = event.imageUri
            if (!uri.isNullOrBlank()) {
                imageView.visibility = android.view.View.VISIBLE
                imageView.setImageURI(android.net.Uri.parse(uri))
            } else {
                imageView.setImageDrawable(null)
                imageView.visibility = android.view.View.GONE
            }

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
                    R.id.action_add_image -> {
                        onAddImageClick(event)
                        true
                    }
                    R.id.action_remove_image -> {
                        onRemoveImageClick(event)
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
