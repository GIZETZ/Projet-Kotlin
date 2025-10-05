package com.example.musep50.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.dao.PaiementWithPayer
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ActivityPublishBinding
import com.example.musep50.viewmodel.DashboardViewModel
import com.example.musep50.viewmodel.PaiementViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PublishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPublishBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val paiementViewModel: PaiementViewModel by viewModels()
    private var currentOperation: Operation? = null
    private var payments = listOf<PaiementWithPayer>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val operationId = intent.getLongExtra("operation_id", -1L)

        setupToolbar()
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

    private fun setupButtons() {
        binding.btnCopyText.setOnClickListener {
            copyToClipboard()
        }

        binding.btnShareWhatsapp.setOnClickListener {
            shareOnWhatsApp()
        }

        binding.btnShareOther.setOnClickListener {
            shareToOtherApps()
        }

        binding.btnExportCsv.setOnClickListener {
            exportToCsv()
        }
    }

    private fun loadData(operationId: Long) {
        dashboardViewModel.allOperations.observe(this) { operations ->
            currentOperation = operations.find { it.id == operationId }
            updatePreview()
        }

        paiementViewModel.getPaiementsWithPayerByOperation(operationId).observe(this) { paymentsData ->
            payments = paymentsData
            updatePreview()
        }
    }

    private fun updatePreview() {
        val operation = currentOperation ?: return
        val text = generateFormattedList(operation, payments)
        binding.previewText.text = text
    }

    private fun generateFormattedList(operation: Operation, payments: List<PaiementWithPayer>): String {
        val builder = StringBuilder()

        builder.appendLine("═══════════════════════════")
        builder.appendLine("📋 MUSEP50 - ${operation.nom}")
        builder.appendLine("═══════════════════════════")
        builder.appendLine()
        builder.appendLine("📅 Période: ${dateFormat.format(operation.dateDebut)} - ${operation.dateFin?.let { dateFormat.format(it) } ?: "N/A"}")
        builder.appendLine("💰 Montant ciblé: ${formatter.format(operation.montantCible)} FCFA")
        builder.appendLine()

        val totalCollecte = payments.sumOf { it.paiement.montant }
        val montantRestant = operation.montantCible - totalCollecte

        builder.appendLine("📊 STATISTIQUES")
        builder.appendLine("───────────────────────────")
        builder.appendLine("✅ Montant collecté: ${formatter.format(totalCollecte)} FCFA")
        builder.appendLine("⏳ Montant restant: ${formatter.format(montantRestant)} FCFA")
        builder.appendLine("👥 Nombre de payeurs: ${payments.size}")
        builder.appendLine()

        builder.appendLine("📝 LISTE DES PAIEMENTS")
        builder.appendLine("───────────────────────────")

        if (payments.isEmpty()) {
            builder.appendLine("Aucun paiement enregistré")
        } else {
            payments.forEachIndexed { index, paiementWithUser ->
                builder.appendLine()
                builder.appendLine("${index + 1}. ${paiementWithUser.payerName} - ${formatter.format(paiementWithUser.paiement.montant)} FCFA")
                builder.appendLine("   📅 Date: ${dateFormat.format(paiementWithUser.paiement.datePaiement)}")
                builder.appendLine("   💳 Méthode: ${paiementWithUser.paiement.methodePaiement}")
                if (!paiementWithUser.paiement.commentaire.isNullOrBlank()) {
                    builder.appendLine("   📝 Note: ${paiementWithUser.paiement.commentaire}")
                }
            }
        }

        builder.appendLine()
        builder.appendLine("═══════════════════════════")
        builder.appendLine("Généré le ${dateFormat.format(Date())}")
        builder.appendLine("═══════════════════════════")

        return builder.toString()
    }

    private fun copyToClipboard() {
        val text = binding.previewText.text.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Liste de paiements", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Texte copié dans le presse-papiers", Toast.LENGTH_SHORT).show()
    }

    private fun shareOnWhatsApp() {
        val text = binding.previewText.text.toString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            setPackage("com.whatsapp")
        }

        try {
            startActivity(sendIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp n'est pas installé", Toast.LENGTH_SHORT).show()
            shareToOtherApps()
        }
    }

    private fun shareToOtherApps() {
        val text = binding.previewText.text.toString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Partager via")
        startActivity(shareIntent)
    }

    private fun exportToCsv() {
        val operation = currentOperation ?: return

        lifecycleScope.launch {
            try {
                val fileName = "musep50_${operation.nom.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

                FileWriter(file).use { writer ->
                    writer.append("Nom du payeur,Contact,Montant (FCFA),Date,Méthode,Commentaire\n")

                    payments.forEach { payment ->
                        writer.append("\"${payment.payerName}\",")
                        writer.append("\"${payment.payer.contact ?: ""}\",")
                        writer.append("${payment.paiement.montant},")
                        writer.append("\"${dateFormat.format(payment.paiement.datePaiement)}\",")
                        writer.append("\"${payment.paiement.methodePaiement}\",")
                        writer.append("\"${payment.paiement.commentaire ?: ""}\"\n")
                    }
                }

                Toast.makeText(
                    this@PublishActivity,
                    "CSV exporté: ${file.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@PublishActivity,
                    "Erreur lors de l'export: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}