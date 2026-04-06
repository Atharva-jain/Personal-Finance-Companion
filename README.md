# 💸 Personal Finance Companion

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-100%25-B125EA?style=for-the-badge&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Modern_UI-4285F4?style=for-the-badge&logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853?style=for-the-badge" alt="MVVM">
  <img src="https://img.shields.io/badge/Concurrency-Coroutines_%26_Flow-blue?style=for-the-badge" alt="Coroutines">
  <img src="https://img.shields.io/badge/DI-Koin-FF9800?style=for-the-badge" alt="Koin">
</p>

## 📱 About The Project

**Personal Finance Companion** is a robust, privacy-first Android application designed to help users take absolute control of their financial habits. Moving beyond traditional, static expense trackers, this application introduces **gamified financial challenges**, visual **savings goals**, and **intelligent background processing** to actively encourage better financial decisions.

Developed entirely in **Kotlin** using **Jetpack Compose**, this project serves as a comprehensive showcase of modern Android development standards. It prioritizes a buttery-smooth user experience (UX) with reactive, offline-first architecture, ensuring user data is secure, persistent, and instantly available.

---

## ✨ Core Features & Functionality

### 🔐 Enterprise-Grade Security
* **Biometric Authentication:** Integrates `androidx.biometric` to intercept the app launch state. Financial data is locked behind device-native fingerprint or facial recognition, governed by securely encrypted `SharedPreferences`.
* **State Preservation:** Biometric state is hoisted and preserved across configuration changes (screen rotations) using `rememberSaveable` to prevent frustrating re-authentication loops.

### 🧠 Intelligent Background Engine (`WorkManager`)
* **Context-Aware Notifications:** Unlike standard alarms, the background workers query the Room Database *before* firing notifications. If a user has already logged an expense for the day, the Daily Reminder is silently canceled to prevent spam.
* **Dynamic Summaries:** Calculates real 7-day trailing expense totals in the background and delivers exact financial figures directly to the Android System Notification Bar.
* **In-App Notification Inbox:** An animated, persistent Inbox screen backed by Room, featuring swipe-to-delete gestures, unread state indicators, and seamless transition animations. Action buttons on system notifications allow users to bypass the home screen and navigate directly to the Inbox or Add Expense screens.

### 🎯 Gamification & Goal Tracking
* **Savings Targets:** Users can set complex financial goals (e.g., "Emergency Fund", "New Laptop"). Progress is calculated dynamically and visually represented via custom UI components.
* **Financial Challenges:** Users opt into active challenges (e.g., "No Spend Weekend"). The app tracks consecutive successful days and alerts the user upon milestone completions.

### 🎨 Fluid User Interface (Jetpack Compose)
* **Single Activity Architecture:** Utilizes `NavHost` and Compose Navigation for instant, fragment-less screen transitions.
* **Premium Micro-interactions:** Features custom `AnimatedVisibility` transitions, `animateColorAsState` for read/unread toggles, and infinite breathing animations for empty states.
* **Dynamic Theming:** Deeply integrated Material Design 3, seamlessly adapting to the device's System Dark/Light modes.

---

## 🛠️ Technical Architecture & Stack

This application strictly adheres to **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** design pattern to ensure scalability, testability, and separation of concerns.

### Tech Stack Details:
* **UI Toolkit:** Jetpack Compose (Declarative UI)
* **Architecture:** MVVM + Clean Architecture
* **Dependency Injection:** Koin (Chosen for its lightweight footprint and Kotlin-native DSL, injecting Repositories, ViewModels, and Preference Managers).
* **Asynchronous Data Streams:** Kotlin Coroutines & `StateFlow` / `SharedFlow` (Ensuring the UI layer reacts instantly to database mutations without blocking the main thread).
* **Local Persistence:** Room Database (SQLite wrapper). Utilizes one-shot `suspend` queries for background workers and continuous `Flow` emissions for active UI observation.

### 📂 Package Structure Preview
```text
com.yourname.financecompanion
├── data/               # The Data Layer
│   ├── database/       # Room Database configuration & migrations
│   ├── dao/            # Data Access Objects (Transaction, Goal, Challenge, Notification)
│   ├── model/          # Room Entities
│   └── repositories/   # Repository pattern implementation (Data fetching & bridging logic)
├── di/                 # Koin Modules (AppModule for ViewModels, Repositories, Prefs)
├── ui/                 # The UI Layer (Jetpack Compose)
│   ├── features/       # Screen-level composables (Home, Settings, Insights, Notifications)
│   ├── components/     # Reusable UI widgets (Cards, Buttons, Charts)
│   └── theme/          # Material 3 Color Schemes & Typography
└── utils/              # The Domain & Utility Layer
    ├── BiometricHelper # Fingerprint hardware abstraction
    ├── NotificationHelper # WorkManager scheduling logic
    └── Constants       # App-wide routing strings and keys
