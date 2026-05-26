# ReadRhythm Android

ReadRhythm is a native Android reading and listening tracker focused on calm session tracking, local-first persistence, and lightweight reading insights.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=111111)
![MVVM](https://img.shields.io/badge/MVVM-Architecture-6B7280?style=flat-square)
![Room](https://img.shields.io/badge/Room-Local%20Persistence-8B5E3C?style=flat-square)
![Material 3](https://img.shields.io/badge/Material%203-UI-6750A4?style=flat-square)

## Screenshots

> Final portfolio screenshots will be added after UI capture.

<p>
  <img src="/screenshots/library.png" width="220" alt="ReadRhythm Library screen" />
  <img src="/screenshots/discover.png" width="220" alt="ReadRhythm Discover screen" />
  <img src="/screenshots/book-detail.png" width="220" alt="ReadRhythm Book Detail screen" />
</p>

<p>
  <img src="/screenshots/add-session.png" width="220" alt="ReadRhythm Add Session dialog" />
  <img src="/screenshots/insights.png" width="220" alt="ReadRhythm Insights screen" />
  <img src="/screenshots/dark-mode.png" width="220" alt="ReadRhythm Dark Mode screen" />
</p>

## Overview

ReadRhythm helps users build a personal reading shelf, log focused reading or listening sessions, and understand their weekly rhythm through simple local insights.

The product direction is calm, practical, and local-first:

- Save books from Google Books into a local library
- Track sessions per title
- Update progress automatically
- Surface lightweight weekly activity
- Keep the experience focused, calm, and easy to scan

## Features

- Discover books via the Google Books API
- Save books to a local Library
- Search and filter the local Library
- Track reading and listening sessions
- Automatically update book progress after each session
- View session history inside each book detail
- See total minutes, session count, active titles, and completed titles
- Review weekly reading activity for the last 7 days
- Support light and dark system themes
- Store data locally with Room

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM + StateFlow
- Room
- Retrofit + Gson
- Google Books API
- Coil
- Navigation Compose
- Coroutines

## Architecture

ReadRhythm uses a lightweight MVVM architecture designed for clear data flow and practical maintainability.

```text
Room / Retrofit
      ↓
Repositories
      ↓
ViewModels
      ↓
StateFlow UI State
      ↓
Jetpack Compose UI
```

Kept intentionally lightweight for MVP maintainability.

Responsibilities stay small and explicit:

- Room stores books and reading sessions locally.
- Retrofit fetches book search results from Google Books.
- Repositories keep data access small and readable.
- ViewModels manage screen state and user actions.
- Compose screens render state and forward events.

The result is a modern Android structure without unnecessary enterprise-style overhead.

## Google Books API Setup

Google Books search can run without an API key, but anonymous requests may be rate-limited.

To use a local API key, add it to `local.properties`:

```properties
GOOGLE_BOOKS_API_KEY=your_api_key_here
```

The key is read through `BuildConfig` and passed as an optional query parameter. It is not committed to the repository.

## Running the App

Clone the repository and open it in Android Studio.

Requirements:

- Android Studio with Kotlin support
- Android SDK
- JDK compatible with the Android Gradle Plugin

Build from the command line:

```bash
./gradlew :app:assembleDebug
```

Optional lint check:

```bash
./gradlew :app:lintDebug
```

## Project Status

ReadRhythm is an actively maintained portfolio-ready MVP focused on product clarity, local-first architecture, and modern Android UI development.

Implemented Features:

- Local Library
- Google Books Discover search
- Add to Library flow with duplicate handling
- Book Detail screen
- Session tracking
- Automatic progress updates
- Insights summary
- Weekly reading activity
- Light and dark theme support

## Intentional Scope Decisions

ReadRhythm is intentionally focused and avoids features that would dilute the core product experience.

The app does not include:

- Social features
- Streaks or gamification systems
- Cloud sync
- Account management
- Complex analytics
- Overengineered architecture layers

These decisions keep the product aligned with its core idea: a calm, local-first reading and listening tracker.

## Future Improvements

- Add an edit flow for local book metadata
- Add a compact session timeline filter
- Add lightweight session editing
