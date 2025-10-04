
import { sql } from "drizzle-orm";
import { sqliteTable, text, integer, real } from "drizzle-orm/sqlite-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const users = sqliteTable("users", {
  id: integer("id").primaryKey({ autoIncrement: true }),
  nom: text("nom").notNull(),
  email: text("email").notNull().unique(),
  telephone: text("telephone"),
  organisation: text("organisation"),
  role: text("role").notNull().default("Membre"),
  pin: text("pin").notNull(),
  createdAt: integer("created_at", { mode: 'timestamp' }).notNull().default(sql`(unixepoch())`),
});

export const operations = sqliteTable("operations", {
  id: integer("id").primaryKey({ autoIncrement: true }),
  nom: text("nom").notNull(),
  type: text("type").notNull(), // "Adhésion", "Cotisation exceptionnelle", "Fonds de caisse"
  montantCible: real("montant_cible").notNull(),
  dateDebut: integer("date_debut", { mode: 'timestamp' }).notNull(),
  dateFin: integer("date_fin", { mode: 'timestamp' }),
  statut: text("statut").notNull().default("En cours"), // "En cours", "Terminée", "Archivée"
  description: text("description"),
  createdAt: integer("created_at", { mode: 'timestamp' }).notNull().default(sql`(unixepoch())`),
});

export const paiements = sqliteTable("paiements", {
  id: integer("id").primaryKey({ autoIncrement: true }),
  operationId: integer("operation_id").notNull().references(() => operations.id),
  userId: integer("user_id").notNull().references(() => users.id),
  montant: real("montant").notNull(),
  montantDu: real("montant_du"),
  methodePaiement: text("methode_paiement").notNull(), // "Espèces", "Mobile Money", "Virement"
  statut: text("statut").notNull().default("Validé"), // "Validé", "En attente", "Rejeté"
  commentaire: text("commentaire"),
  referenceRecu: text("reference_recu"),
  datePaiement: integer("date_paiement", { mode: 'timestamp' }).notNull(),
  createdAt: integer("created_at", { mode: 'timestamp' }).notNull().default(sql`(unixepoch())`),
});

export const parametres = sqliteTable("parametres", {
  id: integer("id").primaryKey({ autoIncrement: true }),
  cle: text("cle").notNull().unique(),
  valeur: text("valeur").notNull(),
  updatedAt: integer("updated_at", { mode: 'timestamp' }).notNull().default(sql`(unixepoch())`),
});

export const insertUserSchema = createInsertSchema(users).pick({
  nom: true,
  email: true,
  telephone: true,
  organisation: true,
  role: true,
  pinHash: true,
});

export const insertOperationSchema = createInsertSchema(operations).omit({
  id: true,
  createdAt: true,
});

export const insertPaiementSchema = createInsertSchema(paiements).omit({
  id: true,
  createdAt: true,
});

export const insertParametreSchema = createInsertSchema(parametres).omit({
  id: true,
  updatedAt: true,
});

export const loginSchema = z.object({
  email: z.string().email(),
  pin: z.string().length(4),
});

export type InsertUser = z.infer<typeof insertUserSchema>;
export type User = typeof users.$inferSelect;
export type LoginCredentials = z.infer<typeof loginSchema>;
export type Operation = typeof operations.$inferSelect;
export type InsertOperation = z.infer<typeof insertOperationSchema>;
export type Paiement = typeof paiements.$inferSelect;
export type InsertPaiement = z.infer<typeof insertPaiementSchema>;
export type Parametre = typeof parametres.$inferSelect;
export type InsertParametre = z.infer<typeof insertParametreSchema>;
