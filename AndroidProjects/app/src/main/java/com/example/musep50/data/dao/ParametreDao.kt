package com.example.musep50.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musep50.data.entities.Parametre

@Dao
interface ParametreDao {
    @Query("SELECT * FROM parametres")
    fun getAllParametres(): LiveData<List<Parametre>>
    
    @Query("SELECT * FROM parametres WHERE cle = :key")
    suspend fun getParametre(key: String): Parametre?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parametre: Parametre)
    
    @Query("UPDATE parametres SET valeur = :value, updatedAt = :timestamp WHERE cle = :key")
    suspend fun updateValue(key: String, value: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun delete(parametre: Parametre)
}
