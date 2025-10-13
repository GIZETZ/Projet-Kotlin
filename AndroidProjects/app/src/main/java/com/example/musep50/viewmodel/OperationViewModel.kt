
package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Operation
import kotlinx.coroutines.launch

class OperationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    fun getOperationById(operationId: Long) = repository.getOperationByIdLive(operationId)

    fun insertOperation(operation: Operation) {
        viewModelScope.launch {
            repository.insertOperation(operation)
        }
    }

    fun updateOperation(operation: Operation) {
        viewModelScope.launch {
            repository.updateOperation(operation)
        }
    }

    fun deleteOperation(operation: Operation) {
        viewModelScope.launch {
            repository.deleteOperation(operation)
        }
    }
}
