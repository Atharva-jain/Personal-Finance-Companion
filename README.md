# 💸 Personal Finance Companion

<p align="left">
  <img src="https://img.shields.io/badge/Kotlin-100%25-B125EA?style=for-the-badge&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Modern_UI-4285F4?style=for-the-badge&logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853?style=for-the-badge" alt="MVVM">
  <img src="https://img.shields.io/badge/Offline-First-FF9800?style=for-the-badge" alt="Offline First">
</p>

## 📖 Project Overview

**Personal Finance Companion** is a modern, privacy-first Android application designed to help users take absolute control of their financial habits. Moving beyond traditional, static expense trackers, this application introduces **gamified financial challenges**, visual **savings goals**, and an **intelligent notification engine** that actively encourages better financial decisions.

Developed as a showcase of modern Android engineering standards, it utilizes a 100% Kotlin, single-activity architecture. It prioritizes a buttery-smooth user experience with a declarative UI built with Jetpack Compose, asynchronous data streams via Coroutines/Flow, and a robust offline-first SQLite database using Room.

---

## 📸 App Gallery

*(Visual walkthrough of core user flows and UI design)*

| Dashboard Overview | Transaction Management | Insights & Analytics | Smart Inbox |
| :---: | :---: | :---: | :---: |
| <img src="link_to_dashboard.jpg" width="200"/> | <img src="link_to_transaction_add_edit.jpg" width="200"/> | <img src="link_to_insights.jpg" width="200"/> | <img src="link_to_inbox.jpg" width="200"/> |
| **Goals & Challenges** | **Custom Settings** | **Biometric Lock** | **Screen Selection/Navigation** |
| <img src="link_to_goals.jpg" width="200"/> | <img src="link_to_settings.jpg" width="200"/> | <img src="link_to_lock.jpg" width="200"/> | <img src="link_to_selection.jpg" width="200"/> |

---

## ✨ Explanation of Features

The application is divided into several core functional areas, designed for security, engagement, and ease of use:

* **🔐 Enterprise-Grade Security (Biometric Lock)**
    * Users can lock their financial data behind device-native biometric authentication (Fingerprint/Face Unlock).
    * The lock state is preserved securely via `SharedPreferences` and survives configuration changes (like screen rotations) to prevent frustrating re-authentication loops.
* **🤖 Intelligent Background Engine (`WorkManager`)**
    * **Context-Aware Reminders:** Background workers query the local database *before* alerting the user. For example, if a user has already logged an expense today, the daily reminder is silently canceled.
    * **In-App Notification Inbox:** A dedicated, persistent UI for users to review past alerts, featuring smooth swipe-to-delete animations and active read/unread state tracking.
* **📊 Comprehensive Transaction & Data Management**
    * **Full CRUD Operations:** Fast, intuitive logging, editing, and deleting of daily transactions categorized by Income/Expense.
    * **Data Export:** Users can easily export their financial history for external analysis, personal backup, or tax purposes, ensuring they always own their data.
    * **Demo/Developer Mode (6-Month Data Generation):** Includes a one-click testing utility to instantly generate 6 months of realistic dummy data, allowing users and reviewers to immediately experience the app's powerful Insights and Analytics charts without manual data entry.
* **🏆 Gamified Financial Challenges**
    * Users can opt into specific financial challenges (e.g., "No Spend Weekend"). The app tracks consecutive successful days and triggers milestone alerts upon completion.
* **🎯 Visual Savings Goals**
    * Allows users to set custom financial targets (e.g., "Emergency Fund"). Progress is dynamically calculated and visually represented via custom Compose UI components.

---

## 🤔 Assumptions & Trade-offs

During development, the following architectural and product assumptions were made to scope the project effectively:

1.  **Privacy First (Offline-Only):** * *Assumption:* Users prefer sensitive financial data to remain strictly on their personal device. 
    * *Trade-off:* Cloud synchronization (Firebase/AWS) was intentionally omitted to prioritize zero-latency offline performance and absolute data privacy. The local Room Database acts as the single source of truth (mitigated by the Data Export feature).
2.  **Biometric Hardware Availability:** * *Assumption:* The target user's device supports strong biometric authentication or a secure device PIN. The app uses the `androidx.biometric` library to securely fall back to the device PIN if biometric hardware is unavailable.
3.  **Periodic Work Constraints:** * *Assumption:* Background notifications (like Daily Summaries) do not require exact, down-to-the-millisecond timing. 
    * *Trade-off:* `WorkManager` is used with flexible execution windows to respect Android's battery optimization (Doze mode) policies, rather than using exact `AlarmManager` triggers which drain the user's battery.

---

## 🏗️ Technical Architecture & Stack

This application strictly adheres to **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** design pattern.

* **UI Toolkit:** Jetpack Compose (Material Design 3, dynamic theming, custom micro-interactions).
* **Dependency Injection:** Koin (Chosen for its lightweight footprint and Kotlin-native DSL, injecting ViewModels, Repositories, and Preference Managers).
* **Asynchronous Data:** Kotlin Coroutines & `StateFlow` (Ensuring the UI layer reacts instantly to database mutations without blocking the main thread).
* **Local Persistence:** Room Database (Utilizing one-shot `suspend` queries for background workers and continuous `Flow` emissions for active UI observation).
* **Background Processing:** `WorkManager` (Handles periodic summaries and one-time triggers).

### 📂 Package Structure
```text
com.yourname.financecompanion
├── data/               # The Data Layer
│   ├── database/       # Room Database configuration & migrations
│   ├── dao/            # Data Access Objects (Transaction, Goal, Challenge, Notification)
│   ├── model/          # Room Entities
│   └── repositories/   # Repository pattern implementation (Data fetching & bridging)
├── di/                 # Koin Modules (AppModule for ViewModels, Repositories, Prefs)
├── ui/                 # The UI Layer (Jetpack Compose)
│   ├── features/       # Screen-level composables (Home, Settings, Insights, Notifications)
│   ├── components/     # Reusable UI widgets (Cards, Buttons, Charts)
│   └── theme/          # Material 3 Color Schemes & Typography
└── utils/              # The Domain & Utility Layer
    ├── BiometricHelper # Fingerprint hardware abstraction
    ├── NotificationHelper # WorkManager scheduling logic
    └── Constants       # App-wide routing strings and keys
