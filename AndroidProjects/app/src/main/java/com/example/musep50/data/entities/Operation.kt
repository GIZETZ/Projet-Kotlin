package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class Operation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val type: String, // ADHESION, COTISATION_EXCEPTIONNELLE, FONDS_CAISSE
    val montantCible: Double,
    val dateDebut: Long,
    val dateFin: Long? = null,
    val statut: String = "En cours", // "En cours", "Terminé", "Clôturé"
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
