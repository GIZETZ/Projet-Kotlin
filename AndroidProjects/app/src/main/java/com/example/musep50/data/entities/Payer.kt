
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
    indices = [Index(value = ["eventId"])]
)
data class Payer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long? = null, // Nullable pour les anciens payeurs non liés à un événement
    val nom: String,
    val contact: String? = null, // Téléphone ou email
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
