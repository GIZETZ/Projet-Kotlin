
import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";

export async function registerRoutes(app: Express): Promise<Server> {
  // Register new user
  app.post("/api/auth/register", async (req, res) => {
    try {
      const { nom, email, telephone, organisation, pin } = req.body;
      
      if (!nom || !email || !pin || pin.length !== 4) {
        return res.status(400).json({ message: "Données invalides" });
      }

      const existingUser = await storage.getUserByEmail(email);
      if (existingUser) {
        return res.status(409).json({ message: "Un compte avec cet email existe déjà" });
      }

      const user = await storage.createUser({
        nom,
        email,
        telephone,
        organisation,
        pin,
      });

      res.json({ 
        message: "Compte créé avec succès",
        user: { id: user.id, nom: user.nom, email: user.email, role: user.role }
      });
    } catch (error) {
      console.error("Registration error:", error);
      res.status(500).json({ message: "Erreur lors de la création du compte" });
    }
  });

  // Login
  app.post("/api/auth/login", async (req, res) => {
    try {
      const { email, pin } = req.body;
      
      if (!email || !pin || pin.length !== 4) {
        return res.status(400).json({ message: "Email et PIN requis" });
      }

      const user = await storage.verifyUserCredentials(email, pin);
      if (!user) {
        return res.status(401).json({ message: "Email ou PIN incorrect" });
      }

      res.json({ 
        message: "Connexion réussie",
        user: { id: user.id, nom: user.nom, email: user.email, role: user.role }
      });
    } catch (error) {
      console.error("Login error:", error);
      res.status(500).json({ message: "Erreur lors de la connexion" });
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
