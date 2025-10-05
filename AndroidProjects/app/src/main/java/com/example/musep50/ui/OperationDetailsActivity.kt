package com.example.musep50.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.data.dao.PaiementWithUser
import com.example.musep50.data.entities.User
import com.example.musep50.databinding.ActivityOperationDetailsBinding
import com.example.musep50.ui.adapter.PaymentAdapter
import com.example.musep50.viewmodel.AuthViewModel
import com.example.musep50.viewmodel.DashboardViewModel
import com.example.musep50.viewmodel.PaiementViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OperationDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperationDetailsBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val paiementViewModel: PaiementViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var adapter: PaymentAdapter
    private var allPayments = listOf<PaiementWithUser>()
    private var allUsers = listOf<User>()
    private val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRANCE)
    private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)
    private var currentOperationId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentOperationId = intent.getLongExtra("operation_id", -1L)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeViewModels()
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

    private fun setupFab() {
        binding.fabAddPayment.setOnClickListener {
            if (allUsers.isNotEmpty()) {
                val dialog = AddPaymentDialog(
                    operationId = currentOperationId,
                    allUsers = allUsers,
                    onPaymentAdded = {
                        dashboardViewModel.loadOperationStats(listOf(currentOperationId))
                    }
                )
                dialog.show(supportFragmentManager, AddPaymentDialog.TAG)
            }
        }

        binding.btnPublish.setOnClickListener {
            val intent = android.content.Intent(this, PublishActivity::class.java)
            intent.putExtra("operation_id", currentOperationId)
            startActivity(intent)
        }
    }

    private fun filterPayments(query: String) {
        val filtered = if (query.isBlank()) {
            allPayments
        } else {
            allPayments.filter { payment ->
                payment.payerName.contains(query, ignoreCase = true) ||
                payment.paiement.methodePaiement.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
    }

    private fun observeViewModels() {
        authViewModel.loginResult.observe(this) { }
        
        paiementViewModel.getPaiementsWithUserByOperation(currentOperationId).observe(this) { payments ->
            allPayments = payments
            adapter.submitList(payments)
        }

        dashboardViewModel.allOperations.observe(this) { operations ->
            val operation = operations.find { it.id == currentOperationId }
            operation?.let {
                binding.operationName.text = it.nom
                binding.statusChip.text = it.statut
                binding.operationDates.text = "${dateFormat.format(it.dateDebut)} - ${
                    it.dateFin?.let { date -> dateFormat.format(date) } ?: "N/A"
                }"
                binding.montantCible.text = "${formatter.format(it.montantCible)} FCFA"
                
                dashboardViewModel.loadOperationStats(listOf(currentOperationId))
            }
        }

        dashboardViewModel.operationStats.observe(this) { stats ->
            val stat = stats[currentOperationId]
            stat?.let {
                binding.montantCollecte.text = "${formatter.format(it.montantCollecte)} FCFA"
                binding.montantRestant.text = "${formatter.format(it.montantRestant)} FCFA"
                binding.progressBar.progress = it.pourcentage
                binding.nombrePayeurs.text = "${it.nombrePaiements} payeur${if (it.nombrePaiements > 1) "s" else ""}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllUsers()
    }

    private fun loadAllUsers() {
        authViewModel.loginResult.observe(this) { }
        
        val sharedPrefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        val database = com.example.musep50.data.AppDatabase.getDatabase(this)
        val repository = com.example.musep50.data.Repository(database)
        
        repository.getAllUsers().observe(this) { users ->
            allUsers = users
        }
    }
}
