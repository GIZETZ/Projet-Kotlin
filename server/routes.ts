
import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";

export async function registerRoutes(app: Express): Promise<Server> {
  // ==================== AUTH ====================
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

  // ==================== OPERATIONS ====================
  app.get("/api/operations", async (req, res) => {
    try {
      const operations = await storage.getAllOperations();
      const operationsWithStats = await Promise.all(
        operations.map(async (op) => {
          const stats = await storage.getOperationStats(op.id);
          return {
            ...op,
            montantCollecte: stats.total,
            nombrePaiements: stats.count,
            montantRestant: op.montantCible - stats.total,
            pourcentage: (stats.total / op.montantCible) * 100,
          };
        })
      );
      res.json(operationsWithStats);
    } catch (error) {
      console.error("Get operations error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération des opérations" });
    }
  });

  app.get("/api/operations/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const operation = await storage.getOperationById(id);
      
      if (!operation) {
        return res.status(404).json({ message: "Opération non trouvée" });
      }

      const stats = await storage.getOperationStats(id);
      res.json({
        ...operation,
        montantCollecte: stats.total,
        nombrePaiements: stats.count,
        montantRestant: operation.montantCible - stats.total,
        pourcentage: (stats.total / operation.montantCible) * 100,
      });
    } catch (error) {
      console.error("Get operation error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération de l'opération" });
    }
  });

  app.post("/api/operations", async (req, res) => {
    try {
      const { nom, type, montantCible, dateDebut, dateFin, description } = req.body;
      
      if (!nom || !type || !montantCible || !dateDebut) {
        return res.status(400).json({ message: "Données invalides" });
      }

      const operation = await storage.createOperation({
        nom,
        type,
        montantCible: parseFloat(montantCible),
        dateDebut: new Date(dateDebut),
        dateFin: dateFin ? new Date(dateFin) : undefined,
        description,
      });

      res.json({ message: "Opération créée avec succès", operation });
    } catch (error) {
      console.error("Create operation error:", error);
      res.status(500).json({ message: "Erreur lors de la création de l'opération" });
    }
  });

  app.put("/api/operations/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const { nom, type, montantCible, dateDebut, dateFin, statut, description } = req.body;
      
      const updateData: any = {};
      if (nom) updateData.nom = nom;
      if (type) updateData.type = type;
      if (montantCible) updateData.montantCible = parseFloat(montantCible);
      if (dateDebut) updateData.dateDebut = new Date(dateDebut);
      if (dateFin) updateData.dateFin = new Date(dateFin);
      if (statut) updateData.statut = statut;
      if (description !== undefined) updateData.description = description;

      const operation = await storage.updateOperation(id, updateData);
      
      if (!operation) {
        return res.status(404).json({ message: "Opération non trouvée" });
      }

      res.json({ message: "Opération mise à jour avec succès", operation });
    } catch (error) {
      console.error("Update operation error:", error);
      res.status(500).json({ message: "Erreur lors de la mise à jour de l'opération" });
    }
  });

  app.delete("/api/operations/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      await storage.deleteOperation(id);
      res.json({ message: "Opération supprimée avec succès" });
    } catch (error) {
      console.error("Delete operation error:", error);
      res.status(500).json({ message: "Erreur lors de la suppression de l'opération" });
    }
  });

  // ==================== PAIEMENTS ====================
  app.get("/api/operations/:operationId/paiements", async (req, res) => {
    try {
      const operationId = parseInt(req.params.operationId);
      const paiements = await storage.getPaiementsByOperation(operationId);
      res.json(paiements);
    } catch (error) {
      console.error("Get paiements error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération des paiements" });
    }
  });

  app.post("/api/paiements", async (req, res) => {
    try {
      const { operationId, userId, montant, montantDu, methodePaiement, statut, commentaire, referenceRecu, datePaiement } = req.body;
      
      if (!operationId || !userId || !montant || !methodePaiement) {
        return res.status(400).json({ message: "Données invalides" });
      }

      const paiement = await storage.createPaiement({
        operationId: parseInt(operationId),
        userId: parseInt(userId),
        montant: parseFloat(montant),
        montantDu: montantDu ? parseFloat(montantDu) : undefined,
        methodePaiement,
        statut,
        commentaire,
        referenceRecu,
        datePaiement: datePaiement ? new Date(datePaiement) : new Date(),
      });

      res.json({ message: "Paiement enregistré avec succès", paiement });
    } catch (error) {
      console.error("Create paiement error:", error);
      res.status(500).json({ message: "Erreur lors de l'enregistrement du paiement" });
    }
  });

  app.put("/api/paiements/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const { montant, montantDu, methodePaiement, statut, commentaire, referenceRecu, datePaiement } = req.body;
      
      const updateData: any = {};
      if (montant) updateData.montant = parseFloat(montant);
      if (montantDu !== undefined) updateData.montantDu = montantDu ? parseFloat(montantDu) : null;
      if (methodePaiement) updateData.methodePaiement = methodePaiement;
      if (statut) updateData.statut = statut;
      if (commentaire !== undefined) updateData.commentaire = commentaire;
      if (referenceRecu !== undefined) updateData.referenceRecu = referenceRecu;
      if (datePaiement) updateData.datePaiement = new Date(datePaiement);

      const paiement = await storage.updatePaiement(id, updateData);
      
      if (!paiement) {
        return res.status(404).json({ message: "Paiement non trouvé" });
      }

      res.json({ message: "Paiement mis à jour avec succès", paiement });
    } catch (error) {
      console.error("Update paiement error:", error);
      res.status(500).json({ message: "Erreur lors de la mise à jour du paiement" });
    }
  });

  app.delete("/api/paiements/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      await storage.deletePaiement(id);
      res.json({ message: "Paiement supprimé avec succès" });
    } catch (error) {
      console.error("Delete paiement error:", error);
      res.status(500).json({ message: "Erreur lors de la suppression du paiement" });
    }
  });

  // ==================== USERS ====================
  app.get("/api/users", async (req, res) => {
    try {
      const users = await storage.getAllUsers();
      res.json(users.map(u => ({ id: u.id, nom: u.nom, email: u.email, telephone: u.telephone, organisation: u.organisation, role: u.role })));
    } catch (error) {
      console.error("Get users error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération des utilisateurs" });
    }
  });

  app.get("/api/users/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const user = await storage.getUserById(id);
      
      if (!user) {
        return res.status(404).json({ message: "Utilisateur non trouvé" });
      }

      res.json({ id: user.id, nom: user.nom, email: user.email, telephone: user.telephone, organisation: user.organisation, role: user.role });
    } catch (error) {
      console.error("Get user error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération de l'utilisateur" });
    }
  });

  app.put("/api/users/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const { nom, email, telephone, organisation, role } = req.body;
      
      const updateData: any = {};
      if (nom) updateData.nom = nom;
      if (email) updateData.email = email;
      if (telephone !== undefined) updateData.telephone = telephone;
      if (organisation !== undefined) updateData.organisation = organisation;
      if (role) updateData.role = role;

      const user = await storage.updateUser(id, updateData);
      
      if (!user) {
        return res.status(404).json({ message: "Utilisateur non trouvé" });
      }

      res.json({ message: "Utilisateur mis à jour avec succès", user: { id: user.id, nom: user.nom, email: user.email, telephone: user.telephone, organisation: user.organisation, role: user.role } });
    } catch (error) {
      console.error("Update user error:", error);
      res.status(500).json({ message: "Erreur lors de la mise à jour de l'utilisateur" });
    }
  });

  // ==================== PARAMETRES ====================
  app.get("/api/parametres", async (req, res) => {
    try {
      const parametres = await storage.getAllParametres();
      res.json(parametres);
    } catch (error) {
      console.error("Get parametres error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération des paramètres" });
    }
  });

  app.get("/api/parametres/:cle", async (req, res) => {
    try {
      const cle = req.params.cle;
      const parametre = await storage.getParametre(cle);
      
      if (!parametre) {
        return res.status(404).json({ message: "Paramètre non trouvé" });
      }

      res.json(parametre);
    } catch (error) {
      console.error("Get parametre error:", error);
      res.status(500).json({ message: "Erreur lors de la récupération du paramètre" });
    }
  });

  app.put("/api/parametres/:cle", async (req, res) => {
    try {
      const cle = req.params.cle;
      const { valeur } = req.body;
      
      if (!valeur) {
        return res.status(400).json({ message: "Valeur requise" });
      }

      const parametre = await storage.setParametre(cle, valeur);
      res.json({ message: "Paramètre mis à jour avec succès", parametre });
    } catch (error) {
      console.error("Update parametre error:", error);
      res.status(500).json({ message: "Erreur lors de la mise à jour du paramètre" });
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
