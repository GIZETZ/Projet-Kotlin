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
import com.example.musep50.data.entities.Payer
import com.example.musep50.databinding.ActivityRetardatairesBinding
import com.example.musep50.ui.adapter.RetardataireAdapter
import com.example.musep50.viewmodel.DashboardViewModel
import com.example.musep50.viewmodel.PaiementViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

// Data class pour représenter un retardataire avec son montant dû
data class RetardataireInfo(
    val payer: Payer,
    val montantDu: Double,      // Montant total que le payeur doit payer
    val montantPaye: Double,    // Montant déjà payé
    val montantRestant: Double  // Montant restant à payer
)

class RetardatairesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetardatairesBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val paiementViewModel: PaiementViewModel by viewModels()
    private lateinit var adapter: RetardataireAdapter
    private var currentOperation: Operation? = null
    private var allPayers = listOf<Payer>()
    private var retardataires = listOf<Payer>()
    private var selectedRetardataires = listOf<Payer>()
    private var retardatairesInfo = mutableMapOf<Long, RetardataireInfo>() // Map payerId -> RetardataireInfo
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
            currentOperation?.let { operation ->
                repository.getPayersByEvent(operation.eventId).observe(this) { payers ->
                    allPayers = payers
                    calculateRetardataires(operationId)
                }
            }
        }
    }

    private fun calculateRetardataires(operationId: Long) {
        paiementViewModel.getPaiementsWithPayerByOperation(operationId).observe(this) { payments ->
            val operation = currentOperation ?: return@observe
            
            // Calculer la somme des paiements par payeur
            val paiementsParPayeur = payments.groupBy { it.paiement.payerId }
                .mapValues { entry -> entry.value.sumOf { it.paiement.montant } }
            
            // Identifier les retardataires (ceux dont la somme payée < solde fixe)
            val retardatairesTemp = mutableListOf<Payer>()
            retardatairesInfo.clear()
            
            allPayers.forEach { payer ->
                // Montant que ce payeur doit payer (personnalisé ou par défaut)
                val montantDu = payer.montantPersonnalise ?: operation.montantParDefautParPayeur
                
                // Montant déjà payé par ce payeur
                val montantPaye = paiementsParPayeur[payer.id] ?: 0.0
                
                // Montant restant à payer
                val montantRestant = montantDu - montantPaye
                
                // Si le montant restant > 0, c'est un retardataire
                if (montantRestant > 0) {
                    retardatairesTemp.add(payer)
                    retardatairesInfo[payer.id] = RetardataireInfo(
                        payer = payer,
                        montantDu = montantDu,
                        montantPaye = montantPaye,
                        montantRestant = montantRestant
                    )
                }
            }
            
            retardataires = retardatairesTemp
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

    private fun generateReminderMessage(payer: Payer): String {
        val operation = currentOperation ?: return ""
        val info = retardatairesInfo[payer.id]
        
        if (info == null) {
            // Fallback si l'info n'est pas disponible
            val montantDu = payer.montantPersonnalise ?: operation.montantParDefautParPayeur
            return """
                Bonjour ${payer.nom},
                
                Nous n'avons pas encore reçu votre paiement pour l'opération "${operation.nom}" (${operation.type}).
                
                📅 Date limite: À confirmer
                💰 Montant attendu: ${formatter.format(montantDu)} FCFA
                
                Merci de bien vouloir régulariser votre situation dans les meilleurs délais.
                
                Cordialement,
                MUSEP50
            """.trimIndent()
        }
        
        // Message avec les informations détaillées
        val messagePayePartiel = if (info.montantPaye > 0) {
            "\n💵 Montant déjà payé: ${formatter.format(info.montantPaye)} FCFA"
        } else {
            ""
        }

        return """
            Bonjour ${payer.nom},
            
            Nous vous rappelons que votre cotisation pour l'opération "${operation.nom}" (${operation.type}) n'est pas encore soldée.
            
            📅 Date limite: À confirmer
            💰 Montant total à payer: ${formatter.format(info.montantDu)} FCFA$messagePayePartiel
            💳 Montant restant dû: ${formatter.format(info.montantRestant)} FCFA
            
            Merci de bien vouloir régulariser votre situation dans les meilleurs délais.
            
            Cordialement,
            MUSEP50
        """.trimIndent()
    }

    private fun sendReminderToUser(payer: Payer) {
        val message = generateReminderMessage(payer)
        val phoneNumber = if (!payer.contact.isNullOrBlank()) {
            "+225${payer.contact}"
        } else {
            null
        }
        shareOnWhatsApp(message, phoneNumber)
    }

    private fun sendRemindersToSelected() {
        if (selectedRetardataires.isEmpty()) return

        if (selectedRetardataires.size == 1) {
            sendReminderToUser(selectedRetardataires[0])
        } else {
            val messages = selectedRetardataires.joinToString("\n\n---\n\n") { payer ->
                generateReminderMessage(payer)
            }
            shareOnWhatsApp(messages, null)
        }
    }

    private fun shareOnWhatsApp(message: String, phoneNumber: String?) {
        try {
            if (!phoneNumber.isNullOrBlank()) {
                val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                val whatsappUrl = "https://wa.me/$cleanNumber?text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(whatsappUrl)
                startActivity(intent)
            } else {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                    setPackage("com.whatsapp")
                }
                startActivity(sendIntent)
            }
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
