package com.example.musep50.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.data.entities.Operation
import com.example.musep50.databinding.ActivityNewOperationBinding
import com.example.musep50.viewmodel.OperationViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewOperationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewOperationBinding
    private val viewModel: OperationViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private var dateDebut: Date? = null
    private var dateFin: Date? = null
    private var eventId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getLongExtra("event_id", -1)

        if (eventId == -1L) {
            Toast.makeText(this, "Erreur: Aucun événement sélectionné", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupTypeDropdown()
        setupDatePickers()
        setupCreateButton()
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

    private fun setupCreateButton() {
        binding.createButton.setOnClickListener {
            val nom = binding.nomInput.text.toString()
            val type = binding.typeInput.text.toString()
            val montantStr = binding.montantInput.text.toString()

            if (validateInputs(nom, type, montantStr)) {
                val montant = montantStr.toDouble()
                val operation = Operation(
                    eventId = eventId,
                    nom = nom,
                    type = type,
                    montantCible = montant,
                    dateDebut = dateDebut?.time ?: System.currentTimeMillis(),
                    dateFin = dateFin?.time,
                    statut = "En cours"
                )

                viewModel.insertOperation(operation)
                Toast.makeText(this, "Opération créée avec succès", Toast.LENGTH_SHORT).show()
                finish()
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