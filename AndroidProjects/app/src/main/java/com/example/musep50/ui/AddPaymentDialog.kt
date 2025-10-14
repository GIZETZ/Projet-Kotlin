package com.example.musep50.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Paiement
import com.example.musep50.databinding.DialogAddPaymentBinding
import com.example.musep50.viewmodel.PaiementViewModel
import com.example.musep50.viewmodel.PayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddPaymentDialog(
    private val operationId: Long,
    private val onPaymentAdded: () -> Unit
) : DialogFragment() {

    private var _binding: DialogAddPaymentBinding? = null
    private val binding get() = _binding!!
    private val paiementViewModel: PaiementViewModel by viewModels()
    private val payerViewModel: PayerViewModel by viewModels()
    private lateinit var repository: Repository
    private var eventId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = Repository(AppDatabase.getDatabase(requireContext()))

        loadEventId()
        setupMethodDropdown()
        setupButtons()
    }

    private fun loadEventId() {
        viewLifecycleOwner.lifecycleScope.launch {
            val operation = repository.getOperationById(operationId)
            operation?.let {
                eventId = it.eventId
                setupPayerInput()
            }
        }
    }

    private fun setupPayerInput() {
        // Load payers from the event
        payerViewModel.getPayersByEvent(eventId).observe(viewLifecycleOwner) { payers ->
            val payerNames = payers.map { it.nom }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payerNames)
            binding.userInput.setAdapter(adapter)
        }

        binding.btnAddPayer.setOnClickListener {
            // Show dialog to add new payer for this event
            lifecycleScope.launch {
                val event = repository.getEventById(eventId)
                event?.let {
                    val dialog = ManageParticipantsDialog(eventId, it.nom)
                    dialog.show(parentFragmentManager, ManageParticipantsDialog.TAG)
                }
            }
        }
    }

    private fun setupMethodDropdown() {
        val methods = arrayOf("Espèces", "Mobile Money", "Virement", "Autre")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, methods)
        binding.methodInput.setAdapter(adapter)
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                savePaiement()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val payerName = binding.userInput.text.toString()
        if (payerName.isBlank()) {
            binding.userInputLayout.error = "Le nom du payeur est requis"
            return false
        }

        val montantStr = binding.montantInput.text.toString()
        if (montantStr.isBlank()) {
            binding.montantInputLayout.error = "Le montant est requis"
            return false
        }

        val method = binding.methodInput.text.toString()
        if (method.isBlank()) {
            binding.methodInputLayout.error = "La méthode de paiement est requise"
            return false
        }

        return true
    }

    private fun savePaiement() {
        val payerName = binding.userInput.text.toString()
        val montant = binding.montantInput.text.toString().toDouble()
        val method = binding.methodInput.text.toString()
        val commentaire = binding.commentaireInput.text?.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            // First, check if payer exists in this event or create new one
            val payers = payerViewModel.getPayersByEventSync(eventId)
            var payerId = payers.find { it.nom.equals(payerName, ignoreCase = true) }?.id

            if (payerId == null) {
                // Create new payer for this event
                val newPayer = com.example.musep50.data.entities.Payer(
                    eventId = eventId,
                    nom = payerName,
                    contact = null,
                    note = null
                )

                payerId = payerViewModel.insertPayer(newPayer)
            }

            val paiement = Paiement(
                operationId = operationId,
                payerId = payerId,
                montant = montant,
                datePaiement = System.currentTimeMillis(),
                methodePaiement = method,
                commentaire = commentaire,
                statut = "Validé"
            )

            paiementViewModel.insertPaiement(paiement,
                onSuccess = {
                    Toast.makeText(requireContext(), "Paiement enregistré", Toast.LENGTH_SHORT).show()
                    onPaymentAdded()
                    dismiss()
                },
                onError = { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPaymentDialog"
    }
}