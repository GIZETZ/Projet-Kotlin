package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Operation

@Dao
interface OperationDao {
    @Query("SELECT * FROM operations ORDER BY createdAt DESC")
    fun getAllOperations(): LiveData<List<Operation>>

    @Query("SELECT * FROM operations WHERE id = :id")
    suspend fun getOperationById(id: Long): Operation?

    @Query("SELECT * FROM operations WHERE id = :id")
    fun getOperationByIdLive(id: Long): LiveData<Operation?>

    @Query("SELECT * FROM operations WHERE statut = 'En cours' ORDER BY createdAt DESC")
    fun getActiveOperations(): LiveData<List<Operation>>

    @Query("SELECT * FROM operations WHERE type = :type ORDER BY createdAt DESC")
    fun getOperationsByType(type: String): LiveData<List<Operation>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(operation: Operation): Long

    @Update
    suspend fun update(operation: Operation)

    @Delete
    suspend fun delete(operation: Operation)

    @Query("UPDATE operations SET statut = 'Clôturé' WHERE id = :operationId")
    suspend fun closeOperation(operationId: Long)

    @Query("SELECT * FROM operations WHERE statut = :statut ORDER BY dateDebut DESC")
    fun getOperationsByState(statut: String): LiveData<List<Operation>>

    @Query("SELECT * FROM operations WHERE statut = :statut ORDER BY dateDebut DESC")
    fun getOperationsByEtat(statut: String): LiveData<List<Operation>>
}