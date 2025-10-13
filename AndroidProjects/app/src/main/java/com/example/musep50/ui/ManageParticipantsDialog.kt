package com.example.musep50.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musep50.data.entities.Payer
import com.example.musep50.databinding.DialogManageParticipantsBinding
import com.example.musep50.ui.adapter.ParticipantAdapter
import com.example.musep50.viewmodel.PayerViewModel
import kotlinx.coroutines.launch

class ManageParticipantsDialog(
    private val eventId: Long,
    private val eventName: String
) : DialogFragment() {

    private var _binding: DialogManageParticipantsBinding? = null
    private val binding get() = _binding!!
    private val payerViewModel: PayerViewModel by viewModels()
    private lateinit var adapter: ParticipantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogManageParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.eventNameText.text = "Événement: $eventName"

        setupRecyclerView()
        setupButtons()
        observeParticipants()
    }

    private fun setupRecyclerView() {
        adapter = ParticipantAdapter { payer ->
            deleteParticipant(payer)
        }

        binding.participantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.participantsRecyclerView.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnAddParticipant.setOnClickListener {
            if (validateInputs()) {
                addParticipant()
            }
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun observeParticipants() {
        payerViewModel.getPayersByEvent(eventId).observe(viewLifecycleOwner) { participants ->
            if (participants.isEmpty()) {
                binding.participantsRecyclerView.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.participantsRecyclerView.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE
                adapter.submitList(participants)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val participantName = binding.payerNameInput.text.toString()
        if (participantName.isBlank()) {
            binding.payerNameInputLayout.error = "Le nom du participant est requis"
            return false
        }
        binding.payerNameInputLayout.error = null
        return true
    }

    private fun addParticipant() {
        val participantName = binding.payerNameInput.text.toString()
        val contact = binding.payerContactInput.text?.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            val newParticipant = Payer(
                eventId = eventId,
                nom = participantName,
                contact = contact
            )

            try {
                payerViewModel.insertPayer(newParticipant)
                Toast.makeText(requireContext(), "Participant ajouté avec succès", Toast.LENGTH_SHORT).show()
                
                binding.payerNameInput.text?.clear()
                binding.payerContactInput.text?.clear()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteParticipant(payer: Payer) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                payerViewModel.deletePayer(payer)
                Toast.makeText(requireContext(), "Participant supprimé", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur lors de la suppression: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ManageParticipantsDialog"
    }
}
