package com.example.musep50.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.entities.Event
import com.example.musep50.databinding.ActivityEditEventBinding
import com.example.musep50.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEventBinding
    private val viewModel: EventViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private var dateDebut: Date? = null
    private var dateFin: Date? = null
    private var eventId: Long = -1
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getLongExtra("event_id", -1)

        if (eventId == -1L) {
            Toast.makeText(this, "Erreur: Aucun événement sélectionné", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupStatusDropdown()
        setupDatePickers()
        setupSaveButton()
        loadEvent()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupStatusDropdown() {
        val statusOptions = arrayOf("En cours", "Terminé", "Planifié", "Archivé")
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

    private fun loadEvent() {
        viewModel.getEventById(eventId).observe(this) { event ->
            if (event != null) {
                currentEvent = event
                populateFields(event)
            } else {
                Toast.makeText(this@EditEventActivity, "Événement introuvable", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun populateFields(event: Event) {
        binding.nameInput.setText(event.nom)
        binding.descriptionInput.setText(event.description ?: "")
        binding.statusInput.setText(event.statut, false)

        dateDebut = Date(event.dateDebut)
        binding.startDateInput.setText(dateFormat.format(dateDebut!!))

        event.dateFin?.let {
            dateFin = Date(it)
            binding.endDateInput.setText(dateFormat.format(dateFin!!))
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveEvent.setOnClickListener {
            if (validateInputs()) {
                updateEvent()
            }
        }
    }

    private fun updateEvent() {
        val nom = binding.nameInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val status = binding.statusInput.text.toString()

        currentEvent?.let { event ->
            val updatedEvent = event.copy(
                nom = nom,
                description = description.ifEmpty { null },
                dateDebut = dateDebut!!.time,
                dateFin = dateFin?.time,
                statut = status.ifEmpty { "En cours" }
            )

            lifecycleScope.launch {
                try {
                    viewModel.updateEvent(updatedEvent)
                    Toast.makeText(this@EditEventActivity, "Événement modifié avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditEventActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
