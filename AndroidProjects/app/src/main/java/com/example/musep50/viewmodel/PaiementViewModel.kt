package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.dao.PaiementWithUser
import com.example.musep50.data.entities.Paiement
import kotlinx.coroutines.launch

class PaiementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    fun getPaiementsWithUserByOperation(operationId: Long): LiveData<List<PaiementWithUser>> {
        return repository.getPaiementsWithUserByOperation(operationId)
    }

    fun insertPaiement(paiement: Paiement, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertPaiement(paiement)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Erreur lors de l'ajout du paiement")
            }
        }
    }

    fun updatePaiement(paiement: Paiement) {
        viewModelScope.launch {
            repository.updatePaiement(paiement)
        }
    }

    fun deletePaiement(paiement: Paiement) {
        viewModelScope.launch {
            repository.deletePaiement(paiement)
        }
    }
}
