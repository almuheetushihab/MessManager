# Mess Manager (Kotlin + Jetpack Compose)

Offline-first mess meal management app: daily meals, bazar, deposits, auto meal rate and member balance.

## Architecture
MVVM + Clean-ish layers, Hilt DI, Room, Navigation Compose, StateFlow.

```
com.messmanager.app
├── core
│   ├── theme/      Theme, Type, Dimens, ExtraColors (all values read from XML resources)
│   └── util/       UiText, date helpers
├── data
│   ├── local/      Room database, entities, DAOs
│   ├── mapper/     Entity <-> domain mappers
│   └── repository/ MessRepositoryImpl
├── domain
│   ├── model/      Pure Kotlin models
│   ├── repository/ MessRepository interface
│   └── usecase/    CalculateMessSummaryUseCase (meal rate + balance)
├── di/             Hilt modules
├── navigation/     Routes, bottom bar, NavHost
├── ui/components/  Reusable composables (cards, dialogs, fields, states)
└── feature/        One package per screen
    ├── home/ meal/ bazar/ deposit/ members/ report/
    │   ├── XxxScreen.kt
    │   ├── XxxViewModel.kt
    │   ├── XxxUiState.kt
    │   └── components/   screen specific composables
```

## No hard coding
- Texts: `res/values/strings.xml`, Bangla in `res/values-bn/strings.xml`
- Colors: `res/values/colors.xml` and `res/values-night/colors.xml` (dark mode)
- Sizes: `res/values/dimens.xml`, used via `MaterialTheme.dimens`
- ViewModels emit `UiText` instead of raw strings

## Run
Open the folder in Android Studio (Koala or newer, JDK 17), let Gradle sync, run the `app` configuration.

## Calculation
- meal rate = total bazar / total meals (current month)
- member balance = deposit - (member meals x meal rate)

## Next steps
Excel/CSV export, month picker, login and cloud sync (Firebase), shared expenses (gas, bua bill), notifications.
