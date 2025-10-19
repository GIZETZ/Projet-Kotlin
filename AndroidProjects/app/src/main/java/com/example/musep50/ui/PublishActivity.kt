package com.example.musep50.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.content.FileProvider
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
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

    private fun exportToPdf(shareAfter: Boolean) {
        val operation = currentOperation ?: return
        val text = binding.previewText.text.toString()

        lifecycleScope.launch {
            try {
                val pdfFile = generatePdfFile(operation, text)
                Toast.makeText(
                    this@PublishActivity,
                    "PDF exporté: ${pdfFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()

                if (shareAfter) {
                    sharePdf(pdfFile)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PublishActivity,
                    "Erreur PDF: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generatePdfFile(operation: Operation, content: String): File {
        val fileName = "musep50_${operation.nom.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val musepDir = File(downloadsDir, "Musep")
        if (!musepDir.exists()) musepDir.mkdirs()

        val file = File(musepDir, fileName)

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF000000.toInt()
            textSize = 12f
            typeface = Typeface.MONOSPACE
        }

        val lines = content.split("\n")
        val doc = PdfDocument()

        var startLine = 0
        var pageNumber = 1
        while (startLine < lines.size) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            val canvas = page.canvas

            val availableWidth = pageWidth - margin * 2
            val availableHeight = pageHeight - margin * 2

            var y = margin

            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
            }
            val header = "MUSEP50 - ${operation.nom}"
            canvas.drawText(header, margin.toFloat(), y.toFloat(), headerPaint)
            y += 24

            val builder = StringBuilder()
            var consumed = 0
            while (startLine + consumed < lines.size) {
                builder.append(lines[startLine + consumed]).append('\n')
                val layout = StaticLayout.Builder.obtain(builder.toString(), 0, builder.length, textPaint, availableWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .build()
                if (layout.height > availableHeight - (y - margin)) {
                    builder.setLength(builder.lastIndexOf('\n'))
                    break
                }
                consumed++
            }

            val layout = StaticLayout.Builder.obtain(builder.toString(), 0, builder.length, textPaint, availableWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false)
                .build()
            canvas.save()
            canvas.translate(margin.toFloat(), y.toFloat())
            layout.draw(canvas)
            canvas.restore()

            doc.finishPage(page)
            startLine += consumed
            pageNumber++
            if (consumed == 0) break
        }

        file.outputStream().use { os ->
            doc.writeTo(os)
        }
        doc.close()
        return file
    }

    private fun sharePdf(file: File) {
        val authority = "${applicationContext.packageName}.fileprovider"
        val uri: Uri = FileProvider.getUriForFile(this, authority, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Partager le PDF"))
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

        binding.btnExportPdf.setOnClickListener {
            exportToPdf(shareAfter = false)
        }

        binding.btnSharePdf.setOnClickListener {
            exportToPdf(shareAfter = true)
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
                        val payerInfo = if (payment.payerContact.isNullOrBlank()) {
                            payment.payerName
                        } else {
                            "${payment.payerName} (${payment.payerContact})"
                        }
                        writer.append("\"${payerInfo}\",")
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