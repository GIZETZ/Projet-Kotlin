package com.example.musep50.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Operation
import com.example.musep50.data.entities.User
import com.example.musep50.databinding.ActivityRetardatairesBinding
import com.example.musep50.ui.adapter.RetardataireAdapter
import com.example.musep50.viewmodel.DashboardViewModel
import com.example.musep50.viewmodel.PaiementViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class RetardatairesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetardatairesBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val paiementViewModel: PaiementViewModel by viewModels()
    private lateinit var adapter: RetardataireAdapter
    private var currentOperation: Operation? = null
    private var allUsers = listOf<User>()
    private var retardataires = listOf<User>()
    private var selectedRetardataires = listOf<User>()
    private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetardatairesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val operationId = intent.getLongExtra("operation_id", -1L)

        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadData(operationId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = RetardataireAdapter(
            onSelectionChanged = { selected ->
                selectedRetardataires = selected
                updateActionCard()
            },
            onSendIndividual = { user ->
                sendReminderToUser(user)
            }
        )
        binding.retardatairesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.retardatairesRecyclerView.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnSelectAll.setOnClickListener {
            if (selectedRetardataires.size == retardataires.size) {
                adapter.clearSelection()
            } else {
                adapter.selectAll()
            }
        }

        binding.btnSendReminders.setOnClickListener {
            sendRemindersToSelected()
        }
    }

    private fun loadData(operationId: Long) {
        val database = AppDatabase.getDatabase(this)
        val repository = Repository(database)

        dashboardViewModel.allOperations.observe(this) { operations ->
            currentOperation = operations.find { it.id == operationId }
        }

        repository.getAllUsers().observe(this) { users ->
            allUsers = users
            calculateRetardataires(operationId)
        }

        paiementViewModel.getPaiementsWithPayerByOperation(operationId).observe(this) { payments ->
            val payerIds = payments.map { it.paiement.payerId }.toSet()
            retardataires = allUsers.filter { !payerIds.contains(it.id) }
            adapter.submitList(retardataires)
            updateRetardatairesCount()
        }
    }

    private fun calculateRetardataires(operationId: Long) {
        paiementViewModel.getPaiementsWithPayerByOperation(operationId).observe(this) { payments ->
            val payerIds = payments.map { it.paiement.payerId }.toSet()
            retardataires = allUsers.filter { !payerIds.contains(it.id) }
            adapter.submitList(retardataires)
            updateRetardatairesCount()
        }
    }

    private fun updateRetardatairesCount() {
        val count = retardataires.size
        binding.retardatairesCount.text = if (count > 0) {
            "$count membre${if (count > 1) "s" else ""} en attente"
        } else {
            "Tous les membres ont payé ! 🎉"
        }
    }

    private fun updateActionCard() {
        val count = selectedRetardataires.size
        if (count > 0) {
            binding.actionCard.visibility = View.VISIBLE
            binding.selectedCount.text = "$count membre${if (count > 1) "s" else ""} sélectionné${if (count > 1) "s" else ""}"
            binding.btnSelectAll.text = "Tout désélectionner"
        } else {
            binding.actionCard.visibility = View.GONE
            binding.btnSelectAll.text = "Tout sélectionner"
        }
    }

    private fun generateReminderMessage(user: User): String {
        val operation = currentOperation ?: return ""
        
        val montant = lifecycleScope.launch {
            val repository = Repository(AppDatabase.getDatabase(this@RetardatairesActivity))
            val montantDu = operation.montantCible / allUsers.size
            montantDu
        }
        
        return """
            Bonjour ${user.nom},
            
            Nous n'avons pas encore reçu votre paiement pour l'opération "${operation.nom}" (${operation.type}).
            
            📅 Date limite: À confirmer
            💰 Montant attendu: À confirmer FCFA
            
            Merci de bien vouloir régulariser votre situation dans les meilleurs délais.
            
            Cordialement,
            MUSEP50
        """.trimIndent()
    }

    private fun sendReminderToUser(user: User) {
        val message = generateReminderMessage(user)
        shareOnWhatsApp(message, user.telephone)
    }

    private fun sendRemindersToSelected() {
        if (selectedRetardataires.isEmpty()) return

        if (selectedRetardataires.size == 1) {
            sendReminderToUser(selectedRetardataires[0])
        } else {
            val messages = selectedRetardataires.joinToString("\n\n---\n\n") { user ->
                generateReminderMessage(user)
            }
            shareOnWhatsApp(messages, null)
        }
    }

    private fun shareOnWhatsApp(message: String, phoneNumber: String?) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                
                if (!phoneNumber.isNullOrBlank()) {
                    val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                    val uri = Uri.parse("https://wa.me/$cleanNumber?text=${Uri.encode(message)}")
                    data = uri
                    setPackage("com.whatsapp")
                } else {
                    setPackage("com.whatsapp")
                }
            }
            startActivity(sendIntent)
        } catch (e: Exception) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Envoyer rappel via")
            startActivity(shareIntent)
        }
    }
}
