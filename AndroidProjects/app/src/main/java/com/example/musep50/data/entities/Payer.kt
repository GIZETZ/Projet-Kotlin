
package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payers")
data class Payer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val contact: String? = null, // Téléphone ou email
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
