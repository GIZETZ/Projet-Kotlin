package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.R
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ActivityDashboardBinding
import com.example.musep50.ui.adapter.OperationAdapter
import com.example.musep50.viewmodel.DashboardViewModel

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: OperationAdapter
    private var allOperations = listOf<Operation>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        observeViewModel()
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
    
    private fun setupSearchBar() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            filterOperations(text.toString())
        }
    }
    
    private fun setupFab() {
        binding.fabNewOperation.setOnClickListener {
            startActivity(Intent(this, NewOperationActivity::class.java))
        }
    }
    
    private fun filterOperations(query: String) {
        val filtered = if (query.isBlank()) {
            allOperations
        } else {
            allOperations.filter {
                it.nom.contains(query, ignoreCase = true) ||
                it.type.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
        updateEmptyState(filtered.isEmpty())
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.operationsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun observeViewModel() {
        viewModel.allOperations.observe(this) { operations ->
            allOperations = operations
            adapter.submitList(operations)
            updateEmptyState(operations.isEmpty())
            
            viewModel.loadOperationStats(operations.map { it.id })
        }
        
        viewModel.operationStats.observe(this) { stats ->
            adapter.setOperationStats(stats)
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
