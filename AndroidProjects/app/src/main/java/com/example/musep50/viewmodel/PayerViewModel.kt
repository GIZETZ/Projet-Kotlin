
package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Payer
import kotlinx.coroutines.launch

class PayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    fun getAllPayers(): LiveData<List<Payer>> {
        return repository.getAllPayers()
    }

    fun searchPayers(query: String): LiveData<List<Payer>> {
        return repository.searchPayers(query)
    }

    suspend fun insertPayer(payer: Payer): Long {
        return repository.insertPayer(payer)
    }

    fun updatePayer(payer: Payer) {
        viewModelScope.launch {
            repository.updatePayer(payer)
        }
    }

    fun deletePayer(payer: Payer) {
        viewModelScope.launch {
            repository.deletePayer(payer)
        }
    }
}
