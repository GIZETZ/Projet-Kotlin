package com.example.musep50.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musep50.data.dao.*
import com.example.musep50.data.entities.*

@Database(
    entities = [
        User::class,
        Event::class,
        Operation::class,
        Paiement::class,
        Payer::class,
        OperationPayer::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun operationDao(): OperationDao
    abstract fun paiementDao(): PaiementDao
    abstract fun payerDao(): PayerDao
    abstract fun operationPayerDao(): OperationPayerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nom TEXT NOT NULL,
                        description TEXT,
                        dateDebut INTEGER NOT NULL,
                        dateFin INTEGER,
                        statut TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO events (id, nom, description, dateDebut, statut, createdAt)
                    VALUES (1, 'Événement historique', 'Opérations existantes avant la mise à jour', ${System.currentTimeMillis()}, 'Archivé', ${System.currentTimeMillis()})
                """.trimIndent())

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS operations_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        eventId INTEGER NOT NULL,
                        nom TEXT NOT NULL,
                        type TEXT NOT NULL,
                        montantCible REAL NOT NULL,
                        dateDebut INTEGER NOT NULL,
                        dateFin INTEGER,
                        statut TEXT NOT NULL,
                        description TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(eventId) REFERENCES events(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_operations_new_eventId ON operations_new(eventId)
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO operations_new (id, eventId, nom, type, montantCible, dateDebut, dateFin, statut, description, createdAt)
                    SELECT id, 1, nom, type, montantCible, dateDebut, dateFin, statut, description, createdAt
                    FROM operations
                """.trimIndent())

                database.execSQL("DROP TABLE operations")
                database.execSQL("ALTER TABLE operations_new RENAME TO operations")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Créer une nouvelle table payers avec la colonne eventId
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS payers_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        eventId INTEGER,
                        nom TEXT NOT NULL,
                        contact TEXT,
                        note TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(eventId) REFERENCES events(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Créer l'index pour eventId
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_payers_eventId ON payers_new(eventId)
                """.trimIndent())

                // Copier les données existantes (eventId sera NULL pour les anciens payeurs)
                database.execSQL("""
                    INSERT INTO payers_new (id, eventId, nom, contact, note, createdAt)
                    SELECT id, NULL, nom, contact, note, createdAt
                    FROM payers
                """.trimIndent())

                // Supprimer l'ancienne table et renommer la nouvelle
                database.execSQL("DROP TABLE payers")
                database.execSQL("ALTER TABLE payers_new RENAME TO payers")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajouter la colonne montantParDefautParPayeur à la table operations si elle n'existe pas
                try {
                    database.execSQL("ALTER TABLE operations ADD COLUMN montantParDefautParPayeur REAL NOT NULL DEFAULT 0")
                } catch (_: Exception) {
                    // La colonne existe déjà; ignorer
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "musep50_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}