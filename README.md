# Android Phone Code Challenge - Music Player

This project is a functional and responsive music player application built for the **Android Phone Code Challenge**. It allows users to search for songs using the Apple iTunes API, view album details, and play song previews with a professional UI.

## 🚀 Key Achievements & Recent Implementations

### 🎨 Brand Identity & Visual Polish
- **Modern UI**: Implementation of a clean, professional theme across all screens using Jetpack Compose Material 3, ensuring an immersive experience.
- **Fluid Interactions**: Custom declarative UI components built for smooth transitions and rich visual feedback when interacting with lists, songs, and albums.

### 🏗 Architecture & Core Technologies
- **Kotlin & Jetpack Compose**: 100% built with Kotlin and modern declarative UI, using Compose encapsulated within Navigation Fragments for a robust architecture.
- **MVVM Pattern**: Strict separation of concerns using `ViewModel`, `StateFlow`, and immutable UI states to guarantee a predictable, reactive data flow.
- **Dependency Injection (Hilt)**: Standardized DI for network components, local storage, repositories, and ViewModels, allowing for highly testable and decoupled code.
- **Coroutines & Flow**: Utilized for asynchronous network requests, seamless state observation, and efficient real-time playback progress polling.
- **Offline & Local Data**: Integrated Room database to efficiently cache recent songs and manage user playback history locally.
- **Network Abstraction**: Implementation of a Repository pattern (`SongRepository`) that fully abstracts the `ItunesApi` (Retrofit) from the domain layer.

### 📱 UI & Layout Optimization
- **Responsive Compose Layouts**: Flexible layout definitions to ensure album art, song lists, and player controls scale harmoniously on modern mobile displays.
- **Seamless Navigation**: Jetpack Navigation component integrated with SafeArgs and Compose views to manage the backstack securely and efficiently between Search, Album Details, and Player screens.

### 🎵 Playback & User Experience
- **ExoPlayer (Media3) Integration**: Robust media playback using **Media3 (ExoPlayer)**, scoped as a Singleton to persist playback state across different screens and handle configuration changes gracefully.
- **Advanced Player Controls**:
  - **Real-time Progress**: Continuous progress tracking and UI syncing using Coroutines.
  - **Playback State Management**: Support for toggling loop mode, skipping, and reliably retaining state across the app.
- **Lifecycle Awareness**: Strict handling of the player's lifecycle, ensuring resources are cleanly released when the application finishes to prevent memory leaks.

### 📦 Code Organization & Quality
- **Unit Testing**: Established a robust testing foundation using **MockK**, **Turbine**, and **Coroutines Test** to verify core logic like ViewModel states, player interactions, and edge cases.
- **SOLID Principles**: Strongly adhered to Single Responsibility across Data Mappers, Repositories, and ViewModels, coupled with clear Dependency Inversion rules.

## 🛠 Tech Stack
- **UI**: Jetpack Compose, Material 3, View Binding (for Activity host)
- **Navigation**: Android Navigation Component (Fragments + SafeArgs)
- **DI**: Dagger Hilt
- **Network**: Retrofit, Moshi, OkHttp (with Logging Interceptor)
- **Local Storage**: Room Database
- **Image Loading**: Coil
- **Media**: Androidx Media3 (ExoPlayer)
- **Testing**: JUnit 4, MockK, Kotlinx Coroutines Test, Turbine, MockWebServer

## 📖 How to Run
1. Clone the repository.
2. Open in **Android Studio (Ladybug or newer)**.
3. Sync Gradle and run on an Android emulator or a physical Phone.
4. Use the Search bar on the Home screen to find artists or songs, tap on a track to play its preview, or view the album details!
