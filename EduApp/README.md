# EduApp

A Jetpack Compose picture-math puzzle game: 5 levels, 6 puzzles each, with
sound feedback, score history, a Web API fun-fact, and optional cloud sync.

## Features

- 5 levels (6 puzzles each), shuffled order every run
- Sound feedback (toggleable), score history with per-entry edit/delete
- Web API fun-fact about your score (numbersapi.com)
- Optional Firestore cloud sync (off by default, safe if unconfigured)
- Remembers last username + sound setting (DataStore)

## Architecture

```
com.example.eduapp
├── EduApplication.kt   Builds the DI container at startup
├── MainActivity.kt     Nav graph + ViewModel creation
├── di/                 Manual DI container (see below)
├── database/           Room: User, AppDao, AppDatabase (singleton)
├── network/            Retrofit: Numbers API + Firestore REST
├── data/               Preferences (DataStore)
├── game/               Puzzle model + PuzzleRepository (data + answers)
├── viewmodel/          AppViewModel (history/network/prefs), GameSessionViewModel (run state)
├── screen/             Landing, Setting, Game, Score, History
├── helper/             Asset image loading, SoundManager
└── ui/theme/           Material3 theme, matched to the app icon
```

**Why manual DI, not Hilt?** For a project this size, one handwritten container
(`di/AppContainer.kt`) keeps every dependency's construction in one readable
place with no extra annotation-processing setup. Screens/ViewModels still
depend on interfaces, not concrete classes - same architectural benefit either way.

## Setup

Open in Android Studio and run - no config needed for the core game, history,
sound, or Web API fact.

**Optional cloud sync:** create a Firebase project, enable Firestore, then fill
in `network/CloudConfig.kt` with your `PROJECT_ID` and `API_KEY`. Leave both
blank to keep it off - checked before any network call, so nothing breaks if unset.

## Testing

- Unit tests (`app/src/test`): `PuzzleRepositoryTest`, `GameSessionViewModelTest`,
  `AppViewModelTest` (fakes for DAO/prefs/network). Run: `./gradlew testDebugUnitTest`
- Instrumented test (`app/src/androidTest`): `AppDaoTest`, real in-memory Room DB.
  Run: `./gradlew connectedDebugAndroidTest`

## Assets

Puzzle images live in `assets/{1..5}/`. Correct answers are hard-coded in
`game/PuzzleRepository.kt` - update them there if you swap in different images.

## Commits

Not something this file can enforce - keep your own commits small and
descriptive (e.g. "Add delete confirmation to History screen"), since that's
graded separately from the code.
