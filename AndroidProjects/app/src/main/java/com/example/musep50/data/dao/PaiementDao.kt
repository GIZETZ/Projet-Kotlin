package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Paiement

@Dao
interface PaiementDao {
    @Query("SELECT * FROM paiements WHERE operationId = :operationId ORDER BY datePaiement DESC")
    fun getPaiementsByOperation(operationId: Long): LiveData<List<Paiement>>
    
    @Query("SELECT * FROM paiements WHERE userId = :userId ORDER BY datePaiement DESC")
    fun getPaiementsByUser(userId: Long): LiveData<List<Paiement>>
    
    @Query("SELECT * FROM paiements WHERE id = :id")
    suspend fun getPaiementById(id: Long): Paiement?
    
    @Query("SELECT SUM(montant) FROM paiements WHERE operationId = :operationId")
    suspend fun getTotalByOperation(operationId: Long): Double?
    
    @Query("SELECT COUNT(*) FROM paiements WHERE operationId = :operationId")
    suspend fun getCountByOperation(operationId: Long): Int
    
    @Query("""
        SELECT paiements.*, users.nom as payerName, users.email as payerEmail 
        FROM paiements 
        INNER JOIN users ON paiements.userId = users.id 
        WHERE paiements.operationId = :operationId 
        ORDER BY paiements.datePaiement DESC
    """)
    fun getPaiementsWithUserByOperation(operationId: Long): LiveData<List<PaiementWithUser>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(paiement: Paiement): Long
    
    @Update
    suspend fun update(paiement: Paiement)
    
    @Delete
    suspend fun delete(paiement: Paiement)
}

data class PaiementWithUser(
    @Embedded val paiement: Paiement,
    val payerName: String,
    val payerEmail: String
)
