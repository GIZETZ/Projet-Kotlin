package com.example.musep50.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val email: String,
    val telephone: String? = null,
    val organisation: String? = null,
    val role: String = "Membre",
    val pin: String,
    val createdAt: Long = System.currentTimeMillis()
)
