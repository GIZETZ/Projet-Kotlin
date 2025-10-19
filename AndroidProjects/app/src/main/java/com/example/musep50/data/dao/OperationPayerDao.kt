package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musep50.data.entities.OperationPayer
import com.example.musep50.data.entities.Payer

@Dao
interface OperationPayerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: OperationPayer)

    @Delete
    suspend fun delete(link: OperationPayer)

    @Query("SELECT p.* FROM payers p INNER JOIN operation_payers op ON p.id = op.payerId WHERE op.operationId = :operationId")
    fun getPayersByOperation(operationId: Long): LiveData<List<Payer>>

    @Query("SELECT COUNT(*) FROM operation_payers WHERE operationId = :operationId AND payerId = :payerId")
    suspend fun exists(operationId: Long, payerId: Long): Int

    @Query("SELECT COUNT(*) FROM operation_payers WHERE operationId = :operationId")
    suspend fun countByOperation(operationId: Long): Int
}
