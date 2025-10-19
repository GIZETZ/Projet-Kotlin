package com.example.musep50.data

import androidx.lifecycle.LiveData
import com.example.musep50.data.dao.PaiementWithPayer
import com.example.musep50.data.entities.*

class Repository(private val database: AppDatabase) {

    // Event methods
    fun getAllEvents(): LiveData<List<Event>> = database.eventDao().getAllEvents()

    suspend fun getEventById(eventId: Long): Event? {
        return database.eventDao().getEventById(eventId)
    }

    fun getEventByIdLive(eventId: Long): LiveData<Event?> {
        return database.eventDao().getEventByIdLive(eventId)
    }

    suspend fun insertEvent(event: Event): Long = database.eventDao().insert(event)

    suspend fun updateEvent(event: Event) = database.eventDao().update(event)

    suspend fun deleteEvent(event: Event) = database.eventDao().delete(event)

    suspend fun archiveEvent(eventId: Long) = database.eventDao().archiveEvent(eventId)

    // Operation methods
    fun getAllOperations(): LiveData<List<Operation>> = database.operationDao().getAllOperations()

    fun getOperationsByType(type: String): LiveData<List<Operation>> =
        database.operationDao().getOperationsByType(type)

    suspend fun getOperationById(id: Long): Operation? =
        database.operationDao().getOperationById(id)

    fun getOperationByIdLive(id: Long): LiveData<Operation?> =
        database.operationDao().getOperationByIdLive(id)

    fun getOperationsByEtat(etat: String): LiveData<List<Operation>> =
        database.operationDao().getOperationsByEtat(etat)

    fun getOperationsByEvent(eventId: Long): LiveData<List<Operation>> =
        database.operationDao().getOperationsByEvent(eventId)

    suspend fun insertOperation(operation: Operation): Long =
        database.operationDao().insert(operation)

    suspend fun updateOperation(operation: Operation) =
        database.operationDao().update(operation)

    suspend fun deleteOperation(operation: Operation) =
        database.operationDao().delete(operation)

    // Payer methods
    fun getAllPayers(): LiveData<List<Payer>> = database.payerDao().getAllPayers()

    suspend fun getAllPayersSync(): List<Payer> {
        return database.payerDao().getAllPayersSync()
    }

    suspend fun getPayerById(id: Long): Payer? = database.payerDao().getPayerById(id)

    fun getPayersByEvent(eventId: Long): LiveData<List<Payer>> =
        database.payerDao().getPayersByEvent(eventId)

    suspend fun getPayersByEventSync(eventId: Long): List<Payer> =
        database.payerDao().getPayersByEventSync(eventId)

    fun searchPayers(query: String): LiveData<List<Payer>> =
        database.payerDao().searchPayers(query)

    fun searchPayersByEvent(eventId: Long, query: String): LiveData<List<Payer>> =
        database.payerDao().searchPayersByEvent(eventId, query)

    suspend fun insertPayer(payer: Payer): Long = database.payerDao().insert(payer)

    suspend fun updatePayer(payer: Payer) = database.payerDao().update(payer)

    suspend fun deletePayer(payer: Payer) = database.payerDao().delete(payer)

    // Paiement methods
    fun getAllPaiements(): LiveData<List<Paiement>> {
        return database.paiementDao().getAllPaiements()
    }

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

    // Operation-Payer (participants d'une opération)
    fun getPayersByOperation(operationId: Long): LiveData<List<Payer>> =
        database.operationPayerDao().getPayersByOperation(operationId)

    suspend fun addPayerToOperation(operationId: Long, payerId: Long) =
        database.operationPayerDao().insert(OperationPayer(operationId, payerId))

    suspend fun removePayerFromOperation(operationId: Long, payerId: Long) =
        database.operationPayerDao().delete(OperationPayer(operationId, payerId))

    suspend fun isPayerInOperation(operationId: Long, payerId: Long): Boolean =
        database.operationPayerDao().exists(operationId, payerId) > 0

    suspend fun countParticipantsByOperation(operationId: Long): Int =
        database.operationPayerDao().countByOperation(operationId)

    // User methods
    suspend fun getUserByEmail(email: String): User? =
        database.userDao().getUserByEmail(email)

    suspend fun getUserById(id: Long): User? =
        database.userDao().getUserById(id)

    suspend fun insertUser(user: User): Long =
        database.userDao().insert(user)

    fun getAllUsers(): LiveData<List<User>> =
        database.userDao().getAllUsers()

    suspend fun updateUser(user: User) =
        database.userDao().update(user)

    suspend fun getOperationStats(operationId: Long): com.example.musep50.viewmodel.OperationStats {
        val total = getTotalByOperation(operationId) ?: 0.0
        val count = getCountByOperation(operationId)
        val operation = getOperationById(operationId)
        return com.example.musep50.viewmodel.OperationStats(
            montantCollecte = total,
            nombrePaiements = count,
            montantCible = operation?.montantCible ?: 0.0
        )
    }
}