# Mess Manager

An offline-first Android application built with **Kotlin** and **Jetpack Compose** for managing bachelor/mess meals, bazar expenses, member deposits, shared utility bills, and monthly balance settlements.

---

## Features

- **Daily Meals**: Track daily breakfast, lunch, dinner, and guest meals with cutoff time rules and admin override.
- **Bazar Tracking**: Record individual or mess-fund bazar expenses with categories, units, and credit (Baki) tracking.
- **Member Deposits**: Record member money deposits (Cash, bKash, Nagad, Bank).
- **Shared Expenses**: Equal or custom split for shared bills (Rent, Gas, WiFi, Electricity, Maid).
- **Monthly Summary & Reports**: Automatic calculation of meal rate and balance settlements with **CSV** and **PDF** export & share capabilities.
- **Settings & Preferences**:
  - **Theme**: System Default, Light, and Dark mode.
  - **Language**: Per-app locale support for System Default, English, and Bangla (বাংলা).
  - **Mess Info & Cutoff Time**: Custom mess name and meal editing cutoff time.
- **Database Backup & Restore**: Full JSON database export and import via Storage Access Framework (SAF) with schema versioning (`schemaVersion: 4`) and transactional safety.

---

## Architecture

Built using **MVVM** architecture following Android best practices:

- **UI**: Jetpack Compose, Material 3, Navigation Compose
- **State**: StateFlow, `collectAsStateWithLifecycle`
- **DI**: Hilt (`@HiltViewModel`, `@Singleton`)
- **Database**: Room Database with migrations & transactions
- **Preferences**: DataStore Preferences
- **Localization**: Multi-language support (`values/strings.xml` & `values-bn/strings.xml`)

---

## Build Instructions

### Prerequisites
- **Android Studio**: Koala (2024.1.1) or newer
- **JDK**: JDK 17
- **Android SDK**: Compile SDK 34, Min SDK 26

### Debug Build
```bash
./gradlew assembleDebug
```

### Run Unit Tests
```bash
./gradlew test
```

### Release Build & Signing Setup
To sign a release APK, create a `keystore.properties` file in the project root directory (not committed to Git):

```properties
storeFile=path/to/release.keystore
storePassword=your_keystore_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Build the release APK:
```bash
./gradlew assembleRelease
```
*(If `keystore.properties` is missing, the build gracefully falls back to debug signing).*

---

## License
Free & Open Source - Mess Manager App.
