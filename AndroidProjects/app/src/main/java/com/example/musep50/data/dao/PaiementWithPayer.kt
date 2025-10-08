
package com.example.musep50.data.dao

import androidx.room.Embedded
import com.example.musep50.data.entities.Paiement

data class PaiementWithPayer(
    @Embedded val paiement: Paiement,
    val payerName: String,
    val payerContact: String?
)
