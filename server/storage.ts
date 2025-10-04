
import { db } from './db';
import { users } from '@shared/schema';
import { eq } from 'drizzle-orm';
import crypto from 'crypto';

function hashPin(pin: string): string {
  return crypto.createHash('sha256').update(pin).digest('hex');
}

export const storage = {
  async createUser(data: { nom: string; email: string; telephone?: string; organisation?: string; role?: string; pin: string }) {
    const pinHash = hashPin(data.pin);
    const [user] = await db.insert(users).values({
      nom: data.nom,
      email: data.email,
      telephone: data.telephone,
      organisation: data.organisation,
      role: data.role || 'Membre',
      pinHash,
    }).returning();
    return user;
  },

  async getUserByEmail(email: string) {
    const [user] = await db.select().from(users).where(eq(users.email, email));
    return user;
  },

  async verifyUserCredentials(email: string, pin: string) {
    const user = await this.getUserByEmail(email);
    if (!user) return null;
    
    const pinHash = hashPin(pin);
    if (user.pinHash === pinHash) {
      return user;
    }
    return null;
  },

  async getAllUsers() {
    return await db.select().from(users);
  }
};
