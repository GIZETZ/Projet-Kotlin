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
- **Prochaines étapes**: Option C (Retardataires)
