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

## 🎉 État du Projet
Toutes les fonctionnalités demandées ont été implémentées avec succès. La nouvelle architecture hiérarchique permet une meilleure organisation des opérations par événement. L'application est prête pour compilation et test dans Android Studio.
