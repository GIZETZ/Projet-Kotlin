package com.example.musep50.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ActivityEditOperationBinding
import com.example.musep50.viewmodel.OperationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditOperationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditOperationBinding
    private val viewModel: OperationViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private var dateDebut: Date? = null
    private var dateFin: Date? = null
    private var operationId: Long = -1
    private var eventId: Long = -1
    private var currentOperation: Operation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        operationId = intent.getLongExtra("operation_id", -1)
        eventId = intent.getLongExtra("event_id", -1)

        if (operationId == -1L || eventId == -1L) {
            Toast.makeText(this, "Erreur: Données invalides", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupTypeDropdown()
        setupStatutDropdown()
        setupDatePickers()
        setupSaveButton()
        loadOperation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTypeDropdown() {
        val types = arrayOf("ADHESION", "COTISATION_EXCEPTIONNELLE", "FONDS_CAISSE")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        binding.typeInput.setAdapter(adapter)
    }

    private fun setupStatutDropdown() {
        val statuts = arrayOf("En cours", "Terminé", "Annulé")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuts)
        binding.statutInput.setAdapter(adapter)
    }

    private fun setupDatePickers() {
        binding.dateDebutInput.setOnClickListener {
            showDatePicker { date ->
                dateDebut = date
                binding.dateDebutInput.setText(dateFormat.format(date))
            }
        }

        binding.dateFinInput.setOnClickListener {
            showDatePicker { date ->
                dateFin = date
                binding.dateFinInput.setText(dateFormat.format(date))
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(calendar.time)
        }, year, month, day).show()
    }

    private fun loadOperation() {
        viewModel.getOperationById(operationId).observe(this) { operation ->
            if (operation != null) {
                currentOperation = operation
                populateFields(operation)
            } else {
                Toast.makeText(this@EditOperationActivity, "Opération introuvable", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun populateFields(operation: Operation) {
        binding.nomInput.setText(operation.nom)
        binding.typeInput.setText(operation.type, false)
        binding.montantInput.setText(operation.montantCible.toString())
        binding.statutInput.setText(operation.statut, false)

        dateDebut = Date(operation.dateDebut)
        binding.dateDebutInput.setText(dateFormat.format(dateDebut!!))

        operation.dateFin?.let {
            dateFin = Date(it)
            binding.dateFinInput.setText(dateFormat.format(dateFin!!))
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val nom = binding.nomInput.text.toString()
            val type = binding.typeInput.text.toString()
            val montantStr = binding.montantInput.text.toString()
            val statut = binding.statutInput.text.toString()

            if (validateInputs(nom, type, montantStr)) {
                updateOperation(nom, type, montantStr.toDouble(), statut)
            }
        }
    }

    private fun updateOperation(nom: String, type: String, montant: Double, statut: String) {
        currentOperation?.let { operation ->
            val updatedOperation = operation.copy(
                nom = nom,
                type = type,
                montantCible = montant,
                dateDebut = dateDebut?.time ?: operation.dateDebut,
                dateFin = dateFin?.time,
                statut = statut.ifEmpty { "En cours" }
            )

            lifecycleScope.launch {
                try {
                    viewModel.updateOperation(updatedOperation)
                    Toast.makeText(this@EditOperationActivity, "Opération modifiée avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditOperationActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(nom: String, type: String, montantStr: String): Boolean {
        if (nom.isBlank()) {
            binding.nomInputLayout.error = "Le nom est requis"
            return false
        }

        if (type.isBlank()) {
            binding.typeInputLayout.error = "Le type est requis"
            return false
        }

        if (montantStr.isBlank()) {
            binding.montantInputLayout.error = "Le montant est requis"
            return false
        }

        if (dateDebut == null) {
            Toast.makeText(this, "Veuillez sélectionner la date de début", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dateFin == null) {
            Toast.makeText(this, "Veuillez sélectionner la date de fin", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dateFin!!.before(dateDebut)) {
            Toast.makeText(this, "La date de fin doit être après la date de début", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
