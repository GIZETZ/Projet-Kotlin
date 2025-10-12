package com.example.musep50.ui

import android.app.DatePickerDialog
import android.os.Bundle
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
            val description = binding.descriptionInput.text.toString()

            if (validateInputs(nom)) {
                val event = Event(
                    nom = nom,
                    description = if (description.isBlank()) null else description,
                    dateDebut = dateDebut?.time ?: System.currentTimeMillis(),
                    dateFin = dateFin?.time,
                    statut = "En cours"
                )

                lifecycleScope.launch {
                    viewModel.insertEvent(event)
                    Toast.makeText(this@NewEventActivity, "Événement créé avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun validateInputs(nom: String): Boolean {
        if (nom.isBlank()) {
            binding.nomInputLayout.error = "Le nom est requis"
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
