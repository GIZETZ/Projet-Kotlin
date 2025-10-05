
package com.example.musep50.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.data.entities.Paiement
import com.example.musep50.databinding.ActivityOperationDetailsBinding
import com.example.musep50.ui.adapter.PaymentAdapter
import com.example.musep50.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OperationDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperationDetailsBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: PaymentAdapter
    private var allPayments = listOf<Paiement>()
    private val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRANCE)
    private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val operationId = intent.getLongExtra("operation_id", -1L)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab(operationId)
        observeViewModel(operationId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PaymentAdapter()
        binding.paymentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.paymentsRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            filterPayments(text.toString())
        }
    }

    private fun setupFab(operationId: Long) {
        binding.fabAddPayment.setOnClickListener {
            // TODO: Open add payment dialog
        }
    }

    private fun filterPayments(query: String) {
        val filtered = if (query.isBlank()) {
            allPayments
        } else {
            allPayments.filter { payment ->
                // Filter by user name when you have user data
                true
            }
        }
        adapter.submitList(filtered)
    }

    private fun observeViewModel(operationId: Long) {
        viewModel.allOperations.observe(this) { operations ->
            val operation = operations.find { it.id == operationId }
            operation?.let {
                binding.operationName.text = it.nom
                binding.statusChip.text = it.statut
                binding.operationDates.text = "${dateFormat.format(it.dateDebut)} - ${dateFormat.format(it.dateFin)}"
                binding.montantCible.text = "${formatter.format(it.montantCible)} FCFA"
            }
        }

        viewModel.operationStats.observe(this) { stats ->
            val stat = stats[operationId]
            stat?.let {
                binding.montantCollecte.text = "${formatter.format(it.montantCollecte)} FCFA"
                binding.montantRestant.text = "${formatter.format(it.montantRestant)} FCFA"
                binding.progressBar.progress = it.pourcentage
                binding.nombrePayeurs.text = "${it.nombrePaiements} payeur${if (it.nombrePaiements > 1) "s" else ""}"
            }
        }
    }
}
