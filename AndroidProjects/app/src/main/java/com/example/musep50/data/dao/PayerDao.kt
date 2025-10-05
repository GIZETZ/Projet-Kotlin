
package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Payer

@Dao
interface PayerDao {
    @Query("SELECT * FROM payers ORDER BY nom ASC")
    fun getAllPayers(): LiveData<List<Payer>>
    
    @Query("SELECT * FROM payers WHERE id = :id")
    suspend fun getPayerById(id: Long): Payer?
    
    @Query("SELECT * FROM payers WHERE nom LIKE '%' || :searchQuery || '%' ORDER BY nom ASC")
    fun searchPayers(searchQuery: String): LiveData<List<Payer>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payer: Payer): Long
    
    @Update
    suspend fun update(payer: Payer)
    
    @Delete
    suspend fun delete(payer: Payer)
}
