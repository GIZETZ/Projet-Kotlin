package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import kotlinx.coroutines.launch

class EventOperationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventOperationsBinding
    private lateinit var repository: Repository
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var adapter: OperationAdapter
    private var eventId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventOperationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        eventId = intent.getLongExtra("event_id", -1)
        
        if (eventId == -1L) {
            finish()
            return
        }
        
        repository = Repository(AppDatabase.getDatabase(this))
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        
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
        adapter = OperationAdapter { operation ->
            val intent = Intent(this, OperationDetailsActivity::class.java)
            intent.putExtra("operation_id", operation.id)
            startActivity(intent)
        }
        
        binding.operationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.operationsRecyclerView.adapter = adapter
    }
    
    private fun setupFab() {
        binding.fabNewOperation.setOnClickListener {
            val intent = Intent(this, NewOperationActivity::class.java)
            intent.putExtra("event_id", eventId)
            startActivity(intent)
        }
    }
    
    private fun loadEventData() {
        lifecycleScope.launch {
            val event = repository.getEventById(eventId)
            event?.let {
                binding.eventName.text = it.nom
                binding.eventDescription.text = it.description ?: "Aucune description"
            }
        }
    }
    
    private fun observeOperations() {
        repository.getOperationsByEvent(eventId).observe(this) { operations ->
            adapter.submitList(operations)
            updateEmptyState(operations.isEmpty())
            
            dashboardViewModel.loadOperationStats(operations.map { it.id })
        }
        
        dashboardViewModel.operationStats.observe(this) { stats ->
            adapter.setOperationStats(stats)
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.operationsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
