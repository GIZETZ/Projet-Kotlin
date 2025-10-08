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

        // Set dialog style
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupPayerInput()
        setupMethodDropdown()
        setupButtons()
    }

    private fun setupPayerInput() {
        // User can type a new payer name or select from existing
        payerViewModel.getAllPayers().observe(viewLifecycleOwner) { payers ->
            val payerNames = payers.map { it.nom }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payerNames)
            binding.userInput.setAdapter(adapter)
        }

        binding.btnAddPayer.setOnClickListener {
            // Show dialog to add new payer
            AddPayerDialog { payerName ->
                // Add the new payer to the adapter and select it
                payerViewModel.insertPayer(com.example.musep50.data.entities.Payer(nom = payerName, contact = null, note = null))
                payerViewModel.getAllPayers().observe(viewLifecycleOwner) { payers ->
                    val payerNames = payers.map { it.nom }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payerNames)
                    binding.userInput.setAdapter(adapter)
                    binding.userInput.setText(payerName, false)
                }
            }.show(parentFragmentManager, AddPayerDialog.TAG)
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
            // First, check if payer exists or create new one
            val payers = payerViewModel.getAllPayersSync()
            var payerId = payers.find { it.nom.equals(payerName, ignoreCase = true) }?.id

            if (payerId == null) {
                // Create new payer
                val newPayer = com.example.musep50.data.entities.Payer(
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