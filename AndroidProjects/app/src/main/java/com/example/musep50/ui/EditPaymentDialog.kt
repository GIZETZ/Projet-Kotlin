
package com.example.musep50.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.dao.PaiementWithPayer
import com.example.musep50.data.entities.Paiement
import com.example.musep50.databinding.DialogAddPaymentBinding
import com.example.musep50.viewmodel.PaiementViewModel
import com.example.musep50.viewmodel.PayerViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditPaymentDialog : DialogFragment() {
    private var _binding: DialogAddPaymentBinding? = null
    private val binding get() = _binding!!
    
    private val paiementViewModel: PaiementViewModel by viewModels()
    private val payerViewModel: PayerViewModel by viewModels()
    
    private var operationId: Long = -1
    private var eventId: Long = -1
    private lateinit var paiementWithPayer: PaiementWithPayer
    private var selectedDate: Long = System.currentTimeMillis()
    
    companion object {
        fun newInstance(operationId: Long, eventId: Long, paiementWithPayer: PaiementWithPayer): EditPaymentDialog {
            val fragment = EditPaymentDialog()
            val args = Bundle().apply {
                putLong("operationId", operationId)
                putLong("eventId", eventId)
                putParcelable("paiementWithPayer", paiementWithPayer)
            }
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operationId = it.getLong("operationId")
            eventId = it.getLong("eventId")
            paiementWithPayer = it.getParcelable("paiementWithPayer")!!
            selectedDate = paiementWithPayer.paiement.datePaiement
        }
    }
    
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
        
        setupPayerDropdown()
        setupPaymentMethods()
        setupDatePicker()
        setupButtons()
        
        // Pré-remplir les champs avec les données existantes
        binding.userInput.setText(paiementWithPayer.payerName)
        binding.montantInput.setText(paiementWithPayer.paiement.montant.toString())
        binding.methodInput.setText(paiementWithPayer.paiement.methodePaiement)
        binding.commentaireInput.setText(paiementWithPayer.paiement.commentaire ?: "")
        
        updateDateDisplay()
    }
    
    private fun setupPayerDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            val payers = payerViewModel.getPayersByEventSync(eventId)
            val payerNames = payers.map { it.nom }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                payerNames
            )
            binding.userInput.setAdapter(adapter)
        }
        
        // Cacher le bouton d'ajout de payeur en mode édition
        binding.btnAddPayer.visibility = View.GONE
    }
    
    private fun setupPaymentMethods() {
        val methods = arrayOf("Espèces", "Mobile Money", "Virement", "Autre")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            methods
        )
        binding.methodInput.setAdapter(adapter)
    }
    
    private fun setupDatePicker() {
        binding.root.findViewById<View>(android.R.id.content)?.let { rootView ->
            val dateButton = com.google.android.material.button.MaterialButton(requireContext()).apply {
                text = "Changer la date"
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16
                }
            }
            
            dateButton.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Sélectionner la date")
                    .setSelection(selectedDate)
                    .build()
                
                datePicker.addOnPositiveButtonClickListener { selection ->
                    selectedDate = selection
                    updateDateDisplay()
                }
                
                datePicker.show(parentFragmentManager, "DATE_PICKER")
            }
            
            (binding.root as? ViewGroup)?.addView(dateButton, 4)
        }
    }
    
    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.FRANCE)
        binding.commentaireInputLayout.helperText = "Date: ${dateFormat.format(selectedDate)}"
    }
    
    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                updatePaiement()
            }
        }
        
        // Ajouter un bouton supprimer
        binding.btnCancel.text = "Supprimer"
        binding.btnCancel.setOnClickListener {
            showDeleteConfirmation()
        }
        
        binding.btnSave.text = "Mettre à jour"
    }
    
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Supprimer le paiement")
            .setMessage("Êtes-vous sûr de vouloir supprimer ce paiement ?")
            .setPositiveButton("Supprimer") { _, _ ->
                deletePaiement()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun deletePaiement() {
        paiementViewModel.deletePaiement(paiementWithPayer.paiement)
        dismiss()
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
    
    private fun updatePaiement() {
        val payerName = binding.userInput.text.toString()
        val montant = binding.montantInput.text.toString().replace(",", ".").toDouble()
        val method = binding.methodInput.text.toString()
        val commentaire = binding.commentaireInput.text?.toString()
        
        viewLifecycleOwner.lifecycleScope.launch {
            val payers = payerViewModel.getPayersByEventSync(eventId)
            val payerId = payers.find { it.nom.equals(payerName, ignoreCase = true) }?.id
                ?: paiementWithPayer.paiement.payerId
            
            val updatedPaiement = paiementWithPayer.paiement.copy(
                payerId = payerId,
                montant = montant,
                methodePaiement = method,
                commentaire = commentaire,
                datePaiement = selectedDate
            )
            
            paiementViewModel.updatePaiement(updatedPaiement)
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
