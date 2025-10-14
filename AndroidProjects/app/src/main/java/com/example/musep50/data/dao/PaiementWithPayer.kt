
package com.example.musep50.data.dao

import android.os.Parcelable
import androidx.room.Embedded
import com.example.musep50.data.entities.Paiement
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaiementWithPayer(
    @Embedded val paiement: Paiement,
    val payerName: String,
    val payerContact: String?
) : Parcelable
