package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operations",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("eventId")]
)
data class Operation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long, // Référence à l'événement parent
    val nom: String,
    val type: String, // ADHESION, COTISATION_EXCEPTIONNELLE, FONDS_CAISSE
    val montantCible: Double,
    val montantParDefautParPayeur: Double = 0.0, // Montant par défaut que chaque payeur doit payer
    val dateDebut: Long,
    val dateFin: Long? = null,
    val statut: String = "En cours", // "En cours", "Terminé", "Clôturé"
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
