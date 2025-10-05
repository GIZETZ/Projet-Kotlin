package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Operation
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    val allOperations: LiveData<List<Operation>> = repository.getAllOperations()

    private val _operationStats = MutableLiveData<Map<Long, OperationStats>>()
    val operationStats: LiveData<Map<Long, OperationStats>> = _operationStats

    fun loadOperationStats(operationIds: List<Long>) {
        viewModelScope.launch {
            val stats = mutableMapOf<Long, OperationStats>()
            operationIds.forEach { id ->
                val total = repository.getTotalByOperation(id) ?: 0.0
                val count = repository.getCountByOperation(id)
                val operation = repository.getOperationById(id)
                operation?.let {
                    stats[id] = OperationStats(
                        montantCollecte = total,
                        nombrePaiements = count,
                        montantCible = it.montantCible
                    )
                }
            }
            _operationStats.value = stats
        }
    }
}

data class OperationStats(
    val montantCollecte: Double,
    val nombrePaiements: Int,
    val montantCible: Double
) {
    val montantRestant: Double
        get() = montantCible - montantCollecte

    val pourcentage: Int
        get() = if (montantCible > 0) ((montantCollecte / montantCible) * 100).toInt() else 0
}