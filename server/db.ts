
import Database from "better-sqlite3";
import { drizzle } from 'drizzle-orm/better-sqlite3';
import * as schema from "@shared/schema";

const sqlite = new Database('app.db');
export const db = drizzle(sqlite, { schema });

// Create tables if they don't exist
sqlite.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    telephone TEXT,
    organisation TEXT,
    role TEXT NOT NULL DEFAULT 'Membre',
    pin TEXT NOT NULL,
    created_at INTEGER NOT NULL DEFAULT (unixepoch())
  );

  CREATE TABLE IF NOT EXISTS operations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    type TEXT NOT NULL,
    montant_cible REAL NOT NULL,
    date_debut INTEGER NOT NULL,
    date_fin INTEGER,
    statut TEXT NOT NULL DEFAULT 'En cours',
    description TEXT,
    created_at INTEGER NOT NULL DEFAULT (unixepoch())
  );

  CREATE TABLE IF NOT EXISTS paiements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    operation_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    montant REAL NOT NULL,
    montant_du REAL,
    methode_paiement TEXT NOT NULL,
    statut TEXT NOT NULL DEFAULT 'Validé',
    commentaire TEXT,
    reference_recu TEXT,
    date_paiement INTEGER NOT NULL,
    created_at INTEGER NOT NULL DEFAULT (unixepoch()),
    FOREIGN KEY (operation_id) REFERENCES operations(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS parametres (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cle TEXT NOT NULL UNIQUE,
    valeur TEXT NOT NULL,
    updated_at INTEGER NOT NULL DEFAULT (unixepoch())
  );
`);
