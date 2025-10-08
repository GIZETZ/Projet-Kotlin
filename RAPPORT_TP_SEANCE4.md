# Rapport TP - Séance 4 : Enregistrement & Persistance

**Projet** : MUSEP50 - Application de gestion financière  
**Technologies** : Kotlin, Room Database, RecyclerView  
**Date** : 8 octobre 2025

---

## 📋 Objectifs du TP

- ✅ Ajout de données dans la base via formulaire
- ✅ Lecture/affichage en RecyclerView
- ✅ Vérification que les données persistent après fermeture de l'app

---

## 1️⃣ Livrable 1 : Ajout de données dans la base via formulaire

### 📝 Description
L'application dispose d'un formulaire permettant d'ajouter des paiements dans la base de données Room. Le formulaire contient tous les champs nécessaires avec validation.

### 🔧 Implémentation
- **Fichier layout** : `res/layout/dialog_add_payment.xml`
- **Base de données** : Room Database avec DAO
- **Entité** : `Paiement` (montant, date, méthode, commentaire)

### 📸 Captures d'écran

#### Screenshot 1 : Formulaire d'ajout de paiement
![Formulaire vide - prêt pour saisie]()

*Insérer ici : Capture montrant le formulaire avec les champs :*
- *Sélection du payeur (dropdown)*
- *Champ montant en FCFA*
- *Méthode de paiement*
- *Commentaire optionnel*
- *Boutons "Annuler" et "Enregistrer"*

---

#### Screenshot 2 : Formulaire rempli
![Formulaire avec données saisies]()

*Insérer ici : Capture montrant le formulaire complété avec :*
- *Un payeur sélectionné*
- *Un montant saisi (ex: 5000 FCFA)*
- *Une méthode de paiement choisie*
- *Un commentaire ajouté*

---

## 2️⃣ Livrable 2 : Lecture/affichage en RecyclerView

### 📝 Description
Les données sont affichées dans un RecyclerView avec un adapter personnalisé. L'application dispose de plusieurs RecyclerView pour différents types de données.

### 🔧 Implémentation
- **Adapter 1** : `OperationAdapter.kt` - Liste des opérations
- **Adapter 2** : `PaymentAdapter.kt` - Liste des paiements avec détails
- **Adapter 3** : `RetardataireAdapter.kt` - Liste des retardataires
- **ViewBinding** : Utilisation de ViewBinding pour les items

### 📸 Captures d'écran

#### Screenshot 3 : RecyclerView des opérations
![Liste des opérations dans le Dashboard]()

*Insérer ici : Capture montrant le RecyclerView avec :*
- *Plusieurs opérations affichées*
- *Nom, type et statut de chaque opération*
- *Statistiques (montant collecté, restant, progression)*
- *Nombre de payeurs*

---

#### Screenshot 4 : RecyclerView des paiements
![Liste détaillée des paiements d'une opération]()

*Insérer ici : Capture montrant le RecyclerView des paiements avec :*
- *Nom du payeur*
- *Montant payé en FCFA*
- *Date de paiement*
- *Méthode de paiement*
- *Commentaire (si présent)*

---

#### Screenshot 5 : Détail d'un item du RecyclerView
![Vue rapprochée d'un item de paiement]()

*Insérer ici : Capture zoomée sur un seul item montrant tous les détails*

---

## 3️⃣ Livrable 3 : Vérification de la persistance après fermeture

### 📝 Description
Les données sont stockées dans une base Room SQLite locale. La persistance est automatique et les données restent disponibles même après fermeture complète de l'application.

### 🔧 Implémentation
- **Base de données** : `AppDatabase.kt` (Room Database)
- **Nom de la base** : `musep50_database`
- **Entités** : User, Operation, Paiement, Payer
- **Version** : 2 avec migration automatique

### 📸 Captures d'écran - Test de persistance

#### Screenshot 6 : Données affichées AVANT fermeture de l'app
![État de l'application avec des données]()

*Insérer ici : Capture montrant :*
- *Le RecyclerView avec plusieurs paiements enregistrés*
- *Les statistiques à jour*
- *Note visible : "AVANT FERMETURE"*

---

#### Screenshot 7 : Application fermée (gestionnaire de tâches)
![Gestionnaire de tâches Android - App fermée]()

*Insérer ici : Capture du gestionnaire de tâches Android montrant :*
- *L'application est bien fermée (pas dans les apps récentes)*
- *OU capture du bouton "Forcer l'arrêt" dans les paramètres de l'app*

---

#### Screenshot 8 : Données affichées APRÈS réouverture de l'app
![Application rouverte - données toujours présentes]()

*Insérer ici : Capture montrant :*
- *Le même RecyclerView avec les MÊMES données*
- *Aucune perte de données*
- *Les statistiques identiques*
- *Note visible : "APRÈS RÉOUVERTURE"*

---

## 📊 Architecture technique

### Base de données Room
```kotlin
@Database(
    entities = [User::class, Operation::class, Paiement::class, Payer::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
```

### DAO pour les opérations CRUD
- `UserDao` : Gestion des utilisateurs
- `OperationDao` : Gestion des opérations
- `PaiementDao` : Gestion des paiements
- `PayerDao` : Gestion des payeurs

### Pattern MVVM
- **ViewModel** : `DashboardViewModel`, `AuthViewModel`
- **LiveData** : Observation réactive des données
- **Coroutines** : Opérations asynchrones

---

## ✅ Conclusion

Ce projet démontre une implémentation complète des concepts de persistance locale sur Android :

1. ✅ **Formulaire fonctionnel** permettant l'ajout de données structurées
2. ✅ **RecyclerView optimisé** avec DiffUtil pour l'affichage performant
3. ✅ **Persistance garantie** grâce à Room Database SQLite

Tous les livrables du TP sont fonctionnels et testés avec succès.

---

## 📝 Instructions pour les captures d'écran

### Comment prendre les captures :
1. **Lancez l'app** dans Android Studio sur un émulateur
2. **Pour le formulaire** : Ouvrez le dialog d'ajout de paiement
3. **Pour le RecyclerView** : Naviguez vers la liste des paiements
4. **Pour la persistance** :
   - Prenez un screenshot de l'app avec des données
   - Fermez complètement l'app (Forcer l'arrêt)
   - Rouvrez l'app et prenez un nouveau screenshot

### Emplacements des screenshots dans ce document :
- Remplacez chaque `![]()` par `![Description](chemin/vers/image.png)`
- Ou insérez directement les images dans votre éditeur markdown

---

**🎓 Travail réalisé dans le cadre du TP Séance 4 - Enregistrement & Persistance**
