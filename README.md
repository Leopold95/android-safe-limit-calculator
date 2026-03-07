# Safe Limit Calculator

Android app that helps users plan daily spending safely until the next income date.

## Overview

**Safe Limit Calculator** computes a safe daily spending limit using:

- current balance,
- next income date,
- mandatory payments,
- minimum reserve.

The limit is recalculated dynamically when user data, payments, or expenses change.

## Key Features

- Splash screen with app initialization flow.
- 2-step onboarding that explains limit logic and mandatory payments.
- Home dashboard with balance, daily limit, upcoming payments, and plan progress.
- Expenses screen with amount/category/date input and limit validation warning.
- Mandatory payments management with add flow and paid/unpaid status.
- Payment details with edit/delete support.
- Analytics screen with overview, planned vs actual, and forecast blocks.
- Settings screen with reserve management, notification toggles, data reset, and app version.
- Bottom navigation: Home / Expenses / Payments / Analytics / Settings.
- Dark UI direction with green accent (`#17D96E`).

## Tech Stack

- **Language:** Kotlin
- **Platform:** Android (minSdk 24, targetSdk 36)
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Compose -> ViewModel -> Repository -> Room/DataStore
- **DI:** Koin
- **Local storage:** Room + DataStore
- **Animation:** Lottie
- **Build:** Gradle Kotlin DSL, KSP, ProGuard/R8 for release

## Project Structure

```text
app/src/main/java/com/alexandr/safelimitcalculator/
  ui/        # screens, composables, viewmodels, navigation
  data/      # models, Room, DataStore, repository
  di/        # Koin modules
  theme/     # LocalAppTheme tokens (colors, typography, shapes, dimensions)
  utils/     # helper utilities (if needed)
```

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 11+
- Android SDK configured

### Run (Windows)

```bat
gradlew.bat :app:assembleDebug
```

or run from Android Studio:

- Open project
- Sync Gradle
- Run `app` configuration on device/emulator

## Build Variants

- `debug` for development
- `release` with minification and resource shrinking enabled

## Configuration Notes

- `applicationId` is configured in Gradle only.
- User-visible text is stored in `app/src/main/res/values/strings.xml`.
- Keep secrets out of source code (`local.properties` / environment variables).

## Roadmap

- System notifications engine (payment due, daily limit exceeded, data update reminder).
- Rich analytics charts and trends.
- Improved forecasting and budgeting insights.
- UI polishing and extended animation coverage.

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

## License

Add your preferred license file (for example, MIT) and update this section accordingly.

