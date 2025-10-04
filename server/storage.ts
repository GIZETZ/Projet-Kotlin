import { db } from './db';
import { users, operations, paiements, parametres } from '@shared/schema';
import { eq, desc, sql, and } from 'drizzle-orm';

type User = typeof users.$inferSelect;

export const storage = {
  // Users
  async createUser(data: { nom: string; email: string; telephone?: string; organisation?: string; role?: string; pin: string }) {
    const [user] = await db.insert(users).values({
      nom: data.nom,
      email: data.email,
      telephone: data.telephone,
      organisation: data.organisation,
      role: data.role || 'Membre',
      pin: data.pin,
    }).returning();
    return user;
  },

  async getUserByEmail(email: string) {
    const [user] = await db.select().from(users).where(eq(users.email, email));
    return user;
  },

  async verifyUserCredentials(email: string, pin: string): Promise<User | null> {
    const user = await db.query.users.findFirst({
      where: eq(users.email, email),
    });

    if (!user) {
      return null;
    }

    // Compare PINs as strings to ensure exact match
    if (user.pin === pin) {
      return user;
    }

    return null;
  },

  async getAllUsers() {
    return await db.select().from(users);
  },

  async getUserById(id: number) {
    const [user] = await db.select().from(users).where(eq(users.id, id));
    return user;
  },

  async updateUser(id: number, data: Partial<{ nom: string; email: string; telephone: string; organisation: string; role: string }>) {
    const [updated] = await db.update(users).set(data).where(eq(users.id, id)).returning();
    return updated;
  },

  // Operations
  async createOperation(data: { nom: string; type: string; montantCible: number; dateDebut: Date; dateFin?: Date; description?: string }) {
    const [operation] = await db.insert(operations).values({
      nom: data.nom,
      type: data.type,
      montantCible: data.montantCible,
      dateDebut: data.dateDebut,
      dateFin: data.dateFin,
      description: data.description,
      statut: 'En cours',
    }).returning();
    return operation;
  },

  async getAllOperations() {
    return await db.select().from(operations).orderBy(desc(operations.createdAt));
  },

  async getOperationById(id: number) {
    const [operation] = await db.select().from(operations).where(eq(operations.id, id));
    return operation;
  },

  async updateOperation(id: number, data: Partial<{ nom: string; type: string; montantCible: number; dateDebut: Date; dateFin?: Date; statut: string; description?: string }>) {
    const [updated] = await db.update(operations).set(data).where(eq(operations.id, id)).returning();
    return updated;
  },

  async deleteOperation(id: number) {
    await db.delete(operations).where(eq(operations.id, id));
  },

  async getOperationStats(operationId: number) {
    const result = await db.select({
      total: sql<number>`COALESCE(SUM(${paiements.montant}), 0)`,
      count: sql<number>`COUNT(${paiements.id})`,
    })
    .from(paiements)
    .where(eq(paiements.operationId, operationId));

    return result[0] || { total: 0, count: 0 };
  },

  // Paiements
  async createPaiement(data: { operationId: number; userId: number; montant: number; montantDu?: number; methodePaiement: string; statut?: string; commentaire?: string; referenceRecu?: string; datePaiement: Date }) {
    const [paiement] = await db.insert(paiements).values({
      operationId: data.operationId,
      userId: data.userId,
      montant: data.montant,
      montantDu: data.montantDu,
      methodePaiement: data.methodePaiement,
      statut: data.statut || 'Validé',
      commentaire: data.commentaire,
      referenceRecu: data.referenceRecu,
      datePaiement: data.datePaiement,
    }).returning();
    return paiement;
  },

  async getPaiementsByOperation(operationId: number) {
    return await db.select({
      paiement: paiements,
      user: users,
    })
    .from(paiements)
    .innerJoin(users, eq(paiements.userId, users.id))
    .where(eq(paiements.operationId, operationId))
    .orderBy(desc(paiements.datePaiement));
  },

  async getPaiementById(id: number) {
    const [paiement] = await db.select().from(paiements).where(eq(paiements.id, id));
    return paiement;
  },

  async updatePaiement(id: number, data: Partial<{ montant: number; montantDu?: number; methodePaiement: string; statut: string; commentaire?: string; referenceRecu?: string; datePaiement: Date }>) {
    const [updated] = await db.update(paiements).set(data).where(eq(paiements.id, id)).returning();
    return updated;
  },

  async deletePaiement(id: number) {
    await db.delete(paiements).where(eq(paiements.id, id));
  },

  // Paramètres
  async getParametre(cle: string) {
    const [param] = await db.select().from(parametres).where(eq(parametres.cle, cle));
    return param;
  },

  async setParametre(cle: string, valeur: string) {
    const existing = await this.getParametre(cle);
    if (existing) {
      const [updated] = await db.update(parametres)
        .set({ valeur, updatedAt: new Date() })
        .where(eq(parametres.cle, cle))
        .returning();
      return updated;
    } else {
      const [created] = await db.insert(parametres)
        .values({ cle, valeur })
        .returning();
      return created;
    }
  },

  async getAllParametres() {
    return await db.select().from(parametres);
  },
};