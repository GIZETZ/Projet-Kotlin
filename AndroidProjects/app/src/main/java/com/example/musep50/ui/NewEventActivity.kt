package com.example.musep50.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.entities.Event
import com.example.musep50.databinding.ActivityNewEventBinding
import com.example.musep50.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewEventBinding
    private val viewModel: EventViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private var dateDebut: Date? = null
    private var dateFin: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupStatusDropdown()
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

    private fun setupStatusDropdown() {
        val statusOptions = arrayOf("En cours", "Terminé", "Planifié")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusOptions)
        binding.statusInput.setAdapter(adapter)
    }

    private fun setupDatePickers() {
        binding.startDateInput.setOnClickListener {
            showDatePicker { date ->
                dateDebut = date
                binding.startDateInput.setText(dateFormat.format(date))
            }
        }

        binding.endDateInput.setOnClickListener {
            showDatePicker { date ->
                dateFin = date
                binding.endDateInput.setText(dateFormat.format(date))
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupCreateButton() {
        binding.btnCreateEvent.setOnClickListener {
            if (validateInputs()) {
                createEvent()
            }
        }
    }

    private fun createEvent() {
        val nom = binding.nameInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val lieu = binding.lieuInput.text.toString()
        val status = binding.statusInput.text.toString()

        val event = Event(
            nom = nom,
            description = description.ifEmpty { null },
            dateDebut = dateDebut!!.time,
            dateFin = dateFin?.time,
            statut = status.ifEmpty { "En cours" }
        )

        lifecycleScope.launch {
            try {
                viewModel.insertEvent(event)
                Toast.makeText(this@NewEventActivity, "Événement créé avec succès", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@NewEventActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val nom = binding.nameInput.text.toString()

        if (nom.isBlank()) {
            Toast.makeText(this, "Veuillez entrer un nom pour l'événement", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dateDebut == null) {
            Toast.makeText(this, "Veuillez sélectionner la date de début", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dateFin != null && dateFin!!.before(dateDebut)) {
            Toast.makeText(this, "La date de fin doit être après la date de début", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}