package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val description: String? = null,
    val dateDebut: Long,
    val dateFin: Long? = null,
    val statut: String = "En cours", // "En cours", "Terminé", "Archivé"
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
