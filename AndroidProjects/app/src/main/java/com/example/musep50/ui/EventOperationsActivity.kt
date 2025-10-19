
package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.R
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ActivityEventOperationsBinding
import com.example.musep50.ui.adapter.OperationAdapter
import com.example.musep50.viewmodel.DashboardViewModel
import com.example.musep50.viewmodel.OperationStats
import com.example.musep50.viewmodel.EventViewModel
import kotlinx.coroutines.launch

class EventOperationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventOperationsBinding
    private lateinit var repository: Repository
    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: OperationAdapter
    private var eventId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventOperationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAppearancePrefs()

        eventId = intent.getLongExtra("event_id", -1)

        if (eventId == -1L) {
            finish()
            return
        }

        repository = Repository(AppDatabase.getDatabase(this))
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadEventData()
        observeOperations()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = OperationAdapter(
            onItemClick = { operation ->
                val intent = Intent(this, OperationDetailsActivity::class.java)
                intent.putExtra("operation_id", operation.id)
                startActivity(intent)
            },
            onEditClick = { operation ->
                editOperation(operation)
            },
            onDeleteClick = { operation ->
                confirmDeleteOperation(operation)
            }
        )

        binding.operationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.operationsRecyclerView.adapter = adapter
    }

    private fun editOperation(operation: Operation) {
        val intent = Intent(this, EditOperationActivity::class.java)
        intent.putExtra("operation_id", operation.id)
        intent.putExtra("event_id", eventId)
        startActivity(intent)
    }

    private fun confirmDeleteOperation(operation: Operation) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer l'opération")
            .setMessage("Êtes-vous sûr de vouloir supprimer \"${operation.nom}\" ? Tous les paiements associés seront également supprimés.")
            .setPositiveButton("Supprimer") { _, _ ->
                deleteOperation(operation)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun deleteOperation(operation: Operation) {
        lifecycleScope.launch {
            repository.deleteOperation(operation)
        }
    }

    private fun setupFab() {
        binding.fabNewOperation.setOnClickListener {
            val intent = Intent(this, NewOperationActivity::class.java)
            intent.putExtra("event_id", eventId)
            startActivity(intent)
        }

        binding.fabManageParticipants.setOnClickListener {
            lifecycleScope.launch {
                val event = repository.getEventById(eventId)
                event?.let {
                    val dialog = ManageParticipantsDialog(eventId, it.nom)
                    dialog.show(supportFragmentManager, ManageParticipantsDialog.TAG)
                }
            }
        }
    }

    private fun loadEventData() {
        lifecycleScope.launch {
            val event = repository.getEventById(eventId)
            event?.let {
                binding.eventName.text = it.nom
                binding.eventDescription.text = it.description
            }
        }
    }

    private fun observeOperations() {
        eventViewModel.getOperationsByEvent(eventId).observe(this) { operations ->
            if (operations.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.operationsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.operationsRecyclerView.visibility = View.VISIBLE
                adapter.submitList(operations)

                // Load statistics for each operation
                lifecycleScope.launch {
                    val stats = mutableMapOf<Long, OperationStats>()
                    operations.forEach { operation ->
                        val operationStats = repository.getOperationStats(operation.id)
                        stats[operation.id] = operationStats
                    }
                    adapter.setOperationStats(stats)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadEventData()
    }
}
