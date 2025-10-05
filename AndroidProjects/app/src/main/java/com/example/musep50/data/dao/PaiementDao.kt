package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Paiement

data class PaiementWithPayer(
    @Embedded val paiement: Paiement,
    val payerName: String,
    val payerContact: String?
)

@Dao
interface PaiementDao {
    @Query("SELECT * FROM paiements WHERE operationId = :operationId ORDER BY datePaiement DESC")
    fun getPaiementsByOperation(operationId: Long): LiveData<List<Paiement>>

    @Query("SELECT * FROM paiements WHERE payerId = :payerId ORDER BY datePaiement DESC")
    fun getPaiementsByPayer(payerId: Long): LiveData<List<Paiement>>

    @Query("SELECT * FROM paiements WHERE id = :id")
    suspend fun getPaiementById(id: Long): Paiement?

    @Query("SELECT SUM(montant) FROM paiements WHERE operationId = :operationId")
    suspend fun getTotalByOperation(operationId: Long): Double?

    @Query("SELECT COUNT(*) FROM paiements WHERE operationId = :operationId")
    suspend fun getCountByOperation(operationId: Long): Int

    @Query("""
        SELECT p.*, payer.nom as payerName, payer.contact as payerContact 
        FROM paiements p 
        LEFT JOIN payers payer ON p.payerId = payer.id 
        WHERE p.operationId = :operationId 
        ORDER BY p.datePaiement DESC
    """)
    fun getPaiementsWithPayerByOperation(operationId: Long): LiveData<List<PaiementWithPayer>>
    
    @Query("SELECT * FROM paiements ORDER BY datePaiement DESC")
    fun getAllPaiements(): LiveData<List<Paiement>>

    @Query("""
        SELECT paiements.*, payers.nom as payerName, payers.contact as payerContact 
        FROM paiements 
        INNER JOIN payers ON paiements.payerId = payers.id 
        WHERE paiements.operationId = :operationId 
        ORDER BY paiements.datePaiement DESC
    """)
    fun getPaiementsWithPayerByOperation(operationId: Long): LiveData<List<PaiementWithPayer>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(paiement: Paiement): Long

    @Update
    suspend fun update(paiement: Paiement)

    @Delete
    suspend fun delete(paiement: Paiement)
}