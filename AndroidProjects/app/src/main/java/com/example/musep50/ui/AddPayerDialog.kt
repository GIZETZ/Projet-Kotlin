
package com.example.musep50.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.entities.Payer
import com.example.musep50.databinding.DialogAddPayerBinding
import com.example.musep50.viewmodel.PayerViewModel
import kotlinx.coroutines.launch

class AddPayerDialog(
    private val onPayerAdded: () -> Unit
) : DialogFragment() {

    private var _binding: DialogAddPayerBinding? = null
    private val binding get() = _binding!!
    private val payerViewModel: PayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                savePayer()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val payerName = binding.payerNameInput.text.toString()
        if (payerName.isBlank()) {
            binding.payerNameInputLayout.error = "Le nom du payeur est requis"
            return false
        }
        binding.payerNameInputLayout.error = null
        return true
    }

    private fun savePayer() {
        val payerName = binding.payerNameInput.text.toString()
        val contact = binding.payerContactInput.text?.toString()
        val note = binding.payerNoteInput.text?.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            val newPayer = Payer(
                nom = payerName,
                contact = contact,
                note = note
            )

            try {
                payerViewModel.insertPayer(newPayer)
                Toast.makeText(requireContext(), "Payeur ajouté avec succès", Toast.LENGTH_SHORT).show()
                onPayerAdded()
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPayerDialog"
    }
}
```
