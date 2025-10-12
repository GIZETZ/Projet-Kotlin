package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.Event
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    val allEvents: LiveData<List<Event>> = repository.getAllEvents()

    fun getOperationsByEvent(eventId: Long) = repository.getOperationsByEvent(eventId)

    suspend fun insertEvent(event: Event): Long {
        return repository.insertEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        repository.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        repository.deleteEvent(event)
    }
}
