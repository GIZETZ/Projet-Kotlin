package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parametres",
    indices = [Index(value = ["cle"], unique = true)]
)
data class Parametre(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cle: String,
    val valeur: String,
    val updatedAt: Long = System.currentTimeMillis()
)
