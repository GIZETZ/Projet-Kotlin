package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payers",
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
data class Payer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val nom: String,
    val contact: String? = null,
    val note: String? = null,
    val montantPersonnalise: Double? = null, // Montant personnalisé pour ce payeur (si différent du montant par défaut)
    val createdAt: Long = System.currentTimeMillis()
)