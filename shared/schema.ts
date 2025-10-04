
import { sql } from "drizzle-orm";
import { sqliteTable, text, integer } from "drizzle-orm/sqlite-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const users = sqliteTable("users", {
  id: integer("id").primaryKey({ autoIncrement: true }),
  nom: text("nom").notNull(),
  email: text("email").notNull().unique(),
  telephone: text("telephone"),
  organisation: text("organisation"),
  role: text("role").notNull().default("Membre"),
  pinHash: text("pin_hash").notNull(),
  createdAt: integer("created_at", { mode: 'timestamp' }).notNull().default(sql`(unixepoch())`),
});

export const insertUserSchema = createInsertSchema(users).pick({
  nom: true,
  email: true,
  telephone: true,
  organisation: true,
  role: true,
  pinHash: true,
});

export const loginSchema = z.object({
  email: z.string().email(),
  pin: z.string().length(4),
});

export type InsertUser = z.infer<typeof insertUserSchema>;
export type User = typeof users.$inferSelect;
export type LoginCredentials = z.infer<typeof loginSchema>;
