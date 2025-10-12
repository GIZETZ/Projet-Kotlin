package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): Event?

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventByIdLive(id: Long): LiveData<Event?>

    @Query("SELECT * FROM events WHERE statut = 'En cours' ORDER BY createdAt DESC")
    fun getActiveEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE statut = :statut ORDER BY dateDebut DESC")
    fun getEventsByState(statut: String): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("UPDATE events SET statut = 'Archivé' WHERE id = :eventId")
    suspend fun archiveEvent(eventId: Long)
}