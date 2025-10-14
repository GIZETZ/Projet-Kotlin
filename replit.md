# Android Project - MUSEP50

## Overview
This is a native Android application written in Kotlin for financial operations and transaction tracking (appears to be for a microfinance institution). The app includes:
- User authentication (login/register)
- Operations management
- Payment tracking
- Dashboard with financial data
- Profile management

## Project Structure
- **Build System**: Gradle with Kotlin DSL
- **Language**: Kotlin
- **Architecture**: MVVM pattern with Room database
- **Key Libraries**:
  - Room (database)
  - Lifecycle components
  - Coroutines
  - Material Design components

## Important Note
**This project cannot run on Replit** because it is a native Android application that requires:
- Android SDK
- Android emulator or physical device
- Android Studio or similar IDE

Replit does not support native Android development. Replit only supports React Native/Expo for mobile development.

## Development Environment
To develop this app, you need:
1. Download Android Studio
2. Open this project in Android Studio
3. Build and run on an emulator or connected Android device

## Project Status
- Import completed on October 05, 2025
- Project files verified and intact
- **XML Build Errors Fixed**: Corrected 4 layout XML files that had blank lines before the XML declaration
- **Kotlin Compilation Errors Fixed**: Fixed type mismatches and coroutine issues
- **Option A - Cycle de base**: ✅ COMPLÉTÉ
  - ✅ Ajout de paiements via dialog
  - ✅ Affichage des paiements avec noms des payeurs
  - ✅ Statistiques en temps réel (montant ciblé/collecté/restant)
  - ✅ Recherche et filtrage des paiements
- **Option B - Publication et Export**: ✅ COMPLÉTÉ
  - ✅ Génération de liste formatée (texte avec statistiques)
  - ✅ Partage WhatsApp en un clic
  - ✅ Partage vers autres applications
  - ✅ Copie dans le presse-papiers
  - ✅ Export CSV avec toutes les données
- **Option C - Retardataires**: ✅ COMPLÉTÉ
  - ✅ Détection automatique des membres n'ayant pas payé
  - ✅ Affichage avec sélection multiple
  - ✅ Messages de rappel personnalisés
  - ✅ Envoi individuel ou groupé via WhatsApp
  - ✅ Bouton d'accès dans les détails de l'opération

## 🔄 Architecture Refactoring - Hiérarchie Événements (October 12, 2025)
**Nouvelle hiérarchie de données : Événements → Opérations → Paiements**

### Changements majeurs :
- **Nouvelle entité Event** : Les opérations sont maintenant organisées sous des événements
- **Relation Event-Operation** : Chaque opération appartient à un événement (clé étrangère eventId)
- **Dashboard redesigné** : Affiche maintenant les événements au lieu des opérations directement
- **Navigation améliorée** : 
  - Dashboard liste les événements
  - Cliquer sur un événement → affiche ses opérations (EventOperationsActivity)
  - Cliquer sur une opération → affiche ses détails et paiements

### Nouveaux composants créés :
1. **Base de données** :
   - `Event.kt` - Nouvelle entité pour les événements
   - `EventDao.kt` - DAO pour les requêtes événements
   - `AppDatabase` - Version 3 avec migration

2. **Couche données** :
   - Repository mis à jour avec méthodes Event
   - `EventViewModel.kt` - ViewModel pour gérer les événements

3. **Interface utilisateur** :
   - `item_event.xml` - Layout pour afficher un événement
   - `EventAdapter.kt` - Adapter pour la liste des événements
   - `DashboardActivity` - Modifié pour afficher les événements
   - `activity_event_operations.xml` - Layout pour les opérations d'un événement
   - `EventOperationsActivity.kt` - Activité pour afficher les opérations d'un événement
   - `activity_new_event.xml` - Layout pour créer un événement
   - `NewEventActivity.kt` - Activité pour créer un nouvel événement
   - `NewOperationActivity` - Mis à jour pour accepter eventId

### Notes importantes :
- **Migration de base de données** : Version 3 avec migration propre (MIGRATION_2_3)
  - ✅ Les données existantes sont PRÉSERVÉES lors de la mise à jour
  - Toutes les opérations existantes sont automatiquement assignées à un événement par défaut "Événement historique" (id=1, statut='Archivé')
  - La migration crée la table events, migre les opérations avec eventId, et préserve tous les paiements
- **Flux de travail** :
  1. Créer un événement (ex: "Assemblée Générale 2024")
  2. Ajouter des opérations à l'événement
  3. Gérer les paiements pour chaque opération

## 🆕 Gestion des Participants par Événement (October 13, 2025)
**Nouvelle fonctionnalité : Participants/Payeurs au niveau de l'événement**

### Changements majeurs :
- **Participants liés aux événements** : Les payeurs peuvent maintenant être associés à un événement spécifique
- **Gestion centralisée** : Un bouton dans l'écran des opérations d'un événement permet de gérer la liste des participants
- **Simplification de l'ajout de paiements** : Les participants d'un événement sont automatiquement disponibles lors de l'ajout d'un paiement dans une opération

### Nouveaux composants créés :
1. **Base de données** :
   - Migration MIGRATION_3_4 - Ajout de la colonne `eventId` dans la table `payers`
   - Relation optionnelle Event-Payer (nullable pour compatibilité avec les anciens payeurs)
   - Version de la base de données : 4

2. **Couche données** :
   - `PayerDao` enrichi avec méthodes pour récupérer les payeurs par événement
   - `Repository` et `PayerViewModel` mis à jour avec méthodes de gestion par événement

3. **Interface utilisateur** :
   - `dialog_manage_participants.xml` - Dialog pour gérer les participants d'un événement
   - `item_participant.xml` - Layout pour afficher un participant dans la liste
   - `ManageParticipantsDialog.kt` - Dialog permettant d'ajouter/supprimer des participants
   - `ParticipantAdapter.kt` - Adapter pour afficher la liste des participants
   - Bouton FAB "Gérer les participants" dans `EventOperationsActivity`
   - `AddPaymentDialog` modifié pour charger automatiquement les participants de l'événement

### Flux de travail :
1. **Gestion des participants** :
   - Ouvrir un événement dans le dashboard
   - Cliquer sur le bouton "Gérer les participants" (icône calendrier en bas à gauche)
   - Ajouter/supprimer des participants pour cet événement

2. **Ajout de paiements** :
   - Dans une opération, cliquer sur "Ajouter un paiement"
   - La liste déroulante affiche automatiquement les participants de l'événement parent
   - Possibilité d'ajouter rapidement un nouveau participant via le bouton "+"

### Avantages :
- **Productivité améliorée** : Plus besoin de retaper les noms des participants pour chaque opération
- **Cohérence des données** : Les participants sont partagés entre toutes les opérations d'un même événement
- **Flexibilité** : Les anciens payeurs sans événement continuent de fonctionner (eventId nullable)

## ✏️ CRUD Complet - Édition et Suppression (October 13, 2025)
**Fonctionnalités CRUD complètes pour Événements et Opérations**

### Changements majeurs :
- **Menus contextuels (3 points)** : Ajout d'un bouton menu dans chaque carte d'événement et d'opération
- **Édition complète** : Possibilité de modifier tous les champs d'un événement ou d'une opération
- **Suppression sécurisée** : Dialogue de confirmation avant suppression avec avertissement sur les données liées
- **Architecture MVVM respectée** : Toutes les opérations passent par Repository → ViewModel → Activity

### Nouveaux composants créés :
1. **Menus XML** :
   - `event_item_menu.xml` - Menu contextuel pour événements (Modifier/Supprimer)
   - `operation_item_menu.xml` - Menu contextuel pour opérations (Modifier/Supprimer)

2. **Activités d'édition** :
   - `EditEventActivity.kt` - Activité pour modifier un événement existant
   - `EditOperationActivity.kt` - Activité pour modifier une opération existante
   - `activity_edit_event.xml` - Layout pour l'édition d'événement
   - `activity_edit_operation.xml` - Layout pour l'édition d'opération

3. **Adapters mis à jour** :
   - `EventAdapter` - Ajout de callbacks onEditClick et onDeleteClick avec menu popup
   - `OperationAdapter` - Ajout de callbacks onEditClick et onDeleteClick avec menu popup

4. **ViewModels enrichis** :
   - `EventViewModel.getEventById()` - Récupération d'un événement spécifique (LiveData)
   - `OperationViewModel.getOperationById()` - Récupération d'une opération spécifique (LiveData)
   - Repository expose `getEventByIdLive()` et `getOperationByIdLive()` pour respecter l'abstraction MVVM

### Flux de travail :
1. **Modifier un événement/opération** :
   - Cliquer sur le bouton menu (3 points) sur une carte
   - Sélectionner "Modifier"
   - Tous les champs sont pré-remplis avec les données existantes
   - Modifier les champs souhaités
   - Cliquer sur "Enregistrer les modifications"

2. **Supprimer un événement/opération** :
   - Cliquer sur le bouton menu (3 points) sur une carte
   - Sélectionner "Supprimer"
   - Confirmer la suppression dans le dialogue
   - Les données liées (opérations/paiements) sont également supprimées

### Améliorations :
- **Expérience utilisateur améliorée** : Actions contextuelles accessibles directement depuis les listes
- **Intégrité des données** : Confirmations avant suppression pour éviter les pertes accidentelles
- **Code maintenable** : Architecture MVVM strictement respectée avec séparation des couches

## 💰 Amélioration Retardataires et Gestion des Montants (October 14, 2025)
**Refonte de la logique des retardataires basée sur les montants réels dus et payés**

### Problèmes corrigés :
- **Ancienne logique incorrecte** : Les retardataires étaient simplement ceux qui n'avaient fait AUCUN paiement, même s'ils avaient payé partiellement
- **Messages de rappel avec montant 0 FCFA** : Le montant affiché dans les rappels WhatsApp était toujours 0 FCFA
- **Pas de gestion du montant par défaut par payeur** : Impossible de définir combien chaque participant doit payer

### Changements majeurs :

1. **Nouvelle logique de calcul des retardataires** :
   - Calcul du montant total payé par chaque participant pour une opération
   - Comparaison avec le solde fixe à payer (montantPersonnalise OU montantParDefautParPayeur)
   - Identification comme retardataire si : `montant payé < solde fixe`
   - Calcul du montant restant dû : `solde fixe - montant payé`

2. **Message de rappel WhatsApp amélioré** :
   - Affiche le montant total à payer
   - Affiche le montant déjà payé (si > 0)
   - **Affiche le montant restant dû** au lieu de simplement le montant total
   - Message clair et détaillé pour le participant

3. **Gestion du montant par défaut de l'opération** :
   - Ajout du champ `montantParDefautParPayeur` dans NewOperationActivity
   - Ajout du champ `montantParDefautParPayeur` dans EditOperationActivity
   - Ce montant définit ce que chaque participant doit payer par défaut
   - Validation robuste acceptant virgules et points comme séparateurs décimaux

4. **Gestion du montant personnalisé par participant** :
   - Utilisation du champ `montantPersonnalise` dans ManageParticipantsDialog
   - Chaque participant peut avoir un montant différent à payer
   - Affichage du montant personnalisé dans la liste des participants
   - Validation robuste avec support des formats français (virgule comme séparateur)

### Validation robuste pour format français :
- **Support des virgules ET des points** : "1000,50" ou "1000.50" sont tous les deux acceptés
- **Messages d'erreur clairs** : Guide l'utilisateur avec des exemples de format valide
- **Réinitialisation des erreurs** : Les messages d'erreur disparaissent quand les données deviennent valides
- **Vérification des valeurs positives** : Les montants doivent être > 0

### Fichiers modifiés :
- `RetardatairesActivity.kt` - Nouvelle logique de calcul et messages améliorés
- `NewOperationActivity.kt` - Ajout du champ montant par défaut par payeur avec validation
- `EditOperationActivity.kt` - Ajout du champ montant par défaut par payeur avec validation
- `ManageParticipantsDialog.kt` - Support du montant personnalisé avec validation
- `ParticipantAdapter.kt` - Affichage du montant personnalisé
- `activity_edit_operation.xml` - Champ montant par défaut par payeur
- `item_participant.xml` - Affichage du montant personnalisé

### Flux de travail complet :
1. **Créer une opération** : Définir le montant ciblé total ET le montant par défaut par payeur
2. **Gérer les participants** : Ajouter des participants avec optionnellement un montant personnalisé (différent du montant par défaut)
3. **Ajouter des paiements** : Les participants paient progressivement
4. **Voir les retardataires** : Le système identifie automatiquement ceux qui n'ont pas soldé leur cotisation
5. **Envoyer des rappels** : Messages WhatsApp détaillés avec montant restant dû

## 🎉 État du Projet
Toutes les fonctionnalités demandées ont été implémentées avec succès. La nouvelle architecture hiérarchique permet une meilleure organisation des opérations par événement. La gestion des participants au niveau de l'événement facilite grandement l'ajout de paiements. Les fonctionnalités CRUD complètes permettent une gestion complète des événements et opérations. La fonctionnalité des retardataires a été complètement refondue pour être basée sur les montants réels dus et payés, avec des messages de rappel détaillés. L'application supporte maintenant les formats numériques français (virgules) et est prête pour compilation et test dans Android Studio.
