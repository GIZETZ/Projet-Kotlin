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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.R
import com.example.musep50.data.entities.Paiement
import com.example.musep50.data.entities.User
import com.example.musep50.databinding.DialogAddPaymentBinding
import com.example.musep50.viewmodel.PaiementViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddPaymentDialog(
    private val operationId: Long,
    private val allUsers: List<User>,
    private val onPaymentAdded: () -> Unit
) : DialogFragment() {

    private var _binding: DialogAddPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaiementViewModel by viewModels()
    private var selectedUser: User? = null

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
        
        setupUserDropdown()
        setupMethodDropdown()
        setupButtons()
    }

    private fun setupUserDropdown() {
        val userNames = allUsers.map { it.nom }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, userNames)
        binding.userInput.setAdapter(adapter)
        
        binding.userInput.setOnItemClickListener { _, _, position, _ ->
            selectedUser = allUsers[position]
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
        if (selectedUser == null) {
            Toast.makeText(requireContext(), "Veuillez sélectionner un payeur", Toast.LENGTH_SHORT).show()
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
        val montant = binding.montantInput.text.toString().toDouble()
        val method = binding.methodInput.text.toString()
        val commentaire = binding.commentaireInput.text?.toString()

        val paiement = Paiement(
            operationId = operationId,
            userId = selectedUser!!.id,
            montant = montant,
            methodePaiement = method,
            commentaire = commentaire,
            statut = "Validé"
        )

        viewModel.insertPaiement(
            paiement = paiement,
            onSuccess = {
                Toast.makeText(requireContext(), "Paiement enregistré avec succès", Toast.LENGTH_SHORT).show()
                onPaymentAdded()
                dismiss()
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    companion object {
        const val TAG = "AddPaymentDialog"
    }
}
