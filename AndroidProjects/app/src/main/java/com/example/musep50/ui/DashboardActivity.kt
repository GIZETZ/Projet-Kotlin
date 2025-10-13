package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.R
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Event
import com.example.musep50.databinding.ActivityDashboardBinding
import com.example.musep50.ui.adapter.EventAdapter
import com.example.musep50.viewmodel.EventViewModel
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private lateinit var repository: Repository
    private var allEvents = listOf<Event>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        repository = Repository(AppDatabase.getDatabase(this))
        
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = EventAdapter(
            onItemClick = { event ->
                val intent = Intent(this, EventOperationsActivity::class.java)
                intent.putExtra("event_id", event.id)
                startActivity(intent)
            },
            onEditClick = { event ->
                editEvent(event)
            },
            onDeleteClick = { event ->
                confirmDeleteEvent(event)
            }
        )
        
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eventsRecyclerView.adapter = adapter
    }

    private fun editEvent(event: Event) {
        val intent = Intent(this, EditEventActivity::class.java)
        intent.putExtra("event_id", event.id)
        startActivity(intent)
    }

    private fun confirmDeleteEvent(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer l'événement")
            .setMessage("Êtes-vous sûr de vouloir supprimer \"${event.nom}\" ? Toutes les opérations associées seront également supprimées.")
            .setPositiveButton("Supprimer") { _, _ ->
                deleteEvent(event)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun deleteEvent(event: Event) {
        lifecycleScope.launch {
            repository.deleteEvent(event)
        }
    }
    
    private fun setupSearchBar() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            filterEvents(text.toString())
        }
    }
    
    private fun setupFab() {
        binding.fabNewEvent.setOnClickListener {
            startActivity(Intent(this, NewEventActivity::class.java))
        }
    }
    
    private fun filterEvents(query: String) {
        val filtered = if (query.isBlank()) {
            allEvents
        } else {
            allEvents.filter {
                it.nom.contains(query, ignoreCase = true) ||
                (it.description?.contains(query, ignoreCase = true) == true)
            }
        }
        adapter.submitList(filtered)
        updateEmptyState(filtered.isEmpty())
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.eventsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun observeViewModel() {
        viewModel.allEvents.observe(this) { events ->
            allEvents = events
            adapter.submitList(events)
            updateEmptyState(events.isEmpty())
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_settings -> {
                // TODO: Open settings
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
