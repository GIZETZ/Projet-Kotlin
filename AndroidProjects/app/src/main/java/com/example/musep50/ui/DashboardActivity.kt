package com.example.musep50.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private var currentUserId: Long = -1L
    private var pendingImageEventId: Long? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        val eventId = pendingImageEventId
        pendingImageEventId = null
        if (uri == null || eventId == null) return@registerForActivityResult
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) { }

        val event = allEvents.find { it.id == eventId } ?: return@registerForActivityResult
        lifecycleScope.launch {
            repository.updateEvent(event.copy(imageUri = uri.toString()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)

            currentUserId = intent.getLongExtra("user_id", -1L)

            if (currentUserId == -1L) {
                Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            setSupportActionBar(binding.toolbar)

            repository = Repository(AppDatabase.getDatabase(this))

            setupRecyclerView()
            setupSearchBar()
            setupFab()
            observeViewModel()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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
            },
            onAddImageClick = { event ->
                pendingImageEventId = event.id
                try {
                    pickImageLauncher.launch(arrayOf("image/*"))
                } catch (e: Exception) {
                    Toast.makeText(this, "Impossible d'ouvrir la galerie", Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveImageClick = { event ->
                lifecycleScope.launch {
                    repository.updateEvent(event.copy(imageUri = null))
                }
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
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("user_id", currentUserId)
                startActivity(intent)
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