package com.example.musep50.data

import androidx.lifecycle.LiveData
import com.example.musep50.data.dao.*
import com.example.musep50.data.entities.*

class Repository(private val database: AppDatabase) {
    
    // User operations
    fun getAllUsers(): LiveData<List<User>> = database.userDao().getAllUsers()
    
    suspend fun getUserById(id: Long): User? = database.userDao().getUserById(id)
    
    suspend fun getUserByEmail(email: String): User? = database.userDao().getUserByEmail(email)
    
    suspend fun verifyCredentials(email: String, pin: String): User? = 
        database.userDao().verifyCredentials(email, pin)
    
    suspend fun insertUser(user: User): Long = database.userDao().insert(user)
    
    suspend fun updateUser(user: User) = database.userDao().update(user)
    
    suspend fun updatePin(userId: Long, newPin: String) = database.userDao().updatePin(userId, newPin)
    
    // Operation operations
    fun getAllOperations(): LiveData<List<Operation>> = database.operationDao().getAllOperations()
    
    suspend fun getOperationById(id: Long): Operation? = database.operationDao().getOperationById(id)
    
    fun getOperationByIdLive(id: Long): LiveData<Operation?> = database.operationDao().getOperationByIdLive(id)
    
    fun getActiveOperations(): LiveData<List<Operation>> = database.operationDao().getActiveOperations()
    
    suspend fun insertOperation(operation: Operation): Long = database.operationDao().insert(operation)
    
    suspend fun updateOperation(operation: Operation) = database.operationDao().update(operation)
    
    suspend fun deleteOperation(operation: Operation) = database.operationDao().delete(operation)
    
    suspend fun closeOperation(operationId: Long) = database.operationDao().closeOperation(operationId)
    
    // Paiement operations
    fun getPaiementsByOperation(operationId: Long): LiveData<List<Paiement>> = 
        database.paiementDao().getPaiementsByOperation(operationId)
    
    fun getPaiementsWithUserByOperation(operationId: Long): LiveData<List<PaiementWithUser>> = 
        database.paiementDao().getPaiementsWithUserByOperation(operationId)
    
    suspend fun getTotalByOperation(operationId: Long): Double = 
        database.paiementDao().getTotalByOperation(operationId) ?: 0.0
    
    suspend fun getCountByOperation(operationId: Long): Int = 
        database.paiementDao().getCountByOperation(operationId)
    
    suspend fun insertPaiement(paiement: Paiement): Long = database.paiementDao().insert(paiement)
    
    suspend fun updatePaiement(paiement: Paiement) = database.paiementDao().update(paiement)
    
    suspend fun deletePaiement(paiement: Paiement) = database.paiementDao().delete(paiement)
    
    // Parametre operations
    fun getAllParametres(): LiveData<List<Parametre>> = database.parametreDao().getAllParametres()
    
    suspend fun getParametre(key: String): Parametre? = database.parametreDao().getParametre(key)
    
    suspend fun setParametre(key: String, value: String) {
        val existing = getParametre(key)
        if (existing != null) {
            database.parametreDao().updateValue(key, value)
        } else {
            database.parametreDao().insert(Parametre(cle = key, valeur = value))
        }
    }
}
