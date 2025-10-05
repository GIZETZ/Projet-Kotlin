package com.example.musep50.data

import androidx.lifecycle.LiveData
import com.example.musep50.data.entities.*

class Repository(private val database: AppDatabase) {

    // Operation methods
    fun getAllOperations(): LiveData<List<Operation>> = database.operationDao().getAllOperations()

    fun getOperationsByType(type: String): LiveData<List<Operation>> =
        database.operationDao().getOperationsByType(type)

    suspend fun getOperationById(id: Long): Operation? =
        database.operationDao().getOperationById(id)

    fun getOperationsByEtat(etat: String): LiveData<List<Operation>> =
        database.operationDao().getOperationsByEtat(etat)

    suspend fun insertOperation(operation: Operation): Long =
        database.operationDao().insert(operation)

    suspend fun updateOperation(operation: Operation) =
        database.operationDao().update(operation)

    suspend fun deleteOperation(operation: Operation) =
        database.operationDao().delete(operation)

    // Payer methods
    fun getAllPayers(): LiveData<List<Payer>> = database.payerDao().getAllPayers()

    suspend fun getPayerById(id: Long): Payer? = database.payerDao().getPayerById(id)

    fun searchPayers(query: String): LiveData<List<Payer>> =
        database.payerDao().searchPayers(query)

    suspend fun insertPayer(payer: Payer): Long = database.payerDao().insert(payer)

    suspend fun updatePayer(payer: Payer) = database.payerDao().update(payer)

    suspend fun deletePayer(payer: Payer) = database.payerDao().delete(payer)

    // Paiement methods
    fun getAllPaiements(): LiveData<List<Paiement>> = database.paiementDao().getAllPaiements()

    fun getPaiementsByOperation(operationId: Long): LiveData<List<Paiement>> =
        database.paiementDao().getPaiementsByOperation(operationId)

    fun getPaiementsWithPayerByOperation(operationId: Long): LiveData<List<PaiementWithPayer>> =
        database.paiementDao().getPaiementsWithPayerByOperation(operationId)

    suspend fun getTotalByOperation(operationId: Long): Double? =
        database.paiementDao().getTotalByOperation(operationId)

    suspend fun getCountByOperation(operationId: Long): Int =
        database.paiementDao().getCountByOperation(operationId)

    suspend fun insertPaiement(paiement: Paiement): Long =
        database.paiementDao().insert(paiement)

    suspend fun updatePaiement(paiement: Paiement) =
        database.paiementDao().update(paiement)

    suspend fun deletePaiement(paiement: Paiement) =
        database.paiementDao().delete(paiement)

    // User methods
    suspend fun getUserByEmail(email: String): User? =
        database.userDao().getUserByEmail(email)

    suspend fun getUserById(id: Long): User? =
        database.userDao().getUserById(id)

    suspend fun insertUser(user: User): Long =
        database.userDao().insert(user)
    
    fun getAllUsers(): LiveData<List<User>> =
        database.userDao().getAllUsers()
}