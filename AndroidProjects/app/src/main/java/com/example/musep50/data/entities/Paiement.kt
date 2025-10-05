package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "paiements",
    foreignKeys = [
        ForeignKey(
            entity = Operation::class,
            parentColumns = ["id"],
            childColumns = ["operationId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("operationId"),
        Index("userId")
    ]
)
data class Paiement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operationId: Long,
    val userId: Long,
    val montant: Double,
    val montantDu: Double? = null,
    val methodePaiement: String, // "Espèces", "Mobile Money", "Virement", "Autre"
    val statut: String = "Validé", // "Validé", "En attente", "Annulé"
    val commentaire: String? = null,
    val referenceRecu: String? = null,
    val datePaiement: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
