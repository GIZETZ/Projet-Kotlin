
package com.example.musep50.data.dao

import com.example.musep50.data.entities.Paiement

data class PaiementWithPayer(
    val paiement: Paiement,
    val payerName: String,
    val payerContact: String?
)
