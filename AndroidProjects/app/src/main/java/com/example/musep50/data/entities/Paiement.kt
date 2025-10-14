
package com.example.musep50.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
            entity = Payer::class,
            parentColumns = ["id"],
            childColumns = ["payerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("operationId"),
        Index("payerId")
    ]
)
data class Paiement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operationId: Long,
    val payerId: Long,
    val montant: Double,
    val montantDu: Double? = null,
    val methodePaiement: String, // "Espèces", "Mobile Money", "Virement", "Autre"
    val statut: String = "Validé", // "Validé", "En attente", "Annulé"
    val commentaire: String? = null,
    val referenceRecu: String? = null,
    val datePaiement: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis
) : Parcelable()
)
