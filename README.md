# DailyLift — Android Motivational Quotes App

A premium, Play Store-ready Jetpack Compose app that delivers one beautiful motivational quote every day. This is the Android version of the DailyLift iOS app, built with Kotlin and Material 3.

---

## Requirements

- Android Studio Ladybug (2024.2.1) or later
- Android SDK 35 (API level 35)
- Kotlin 2.1.0+
- Minimum SDK: API 26 (Android 8.0 Oreo)
- JDK 17

---

## Project Structure

```
DailyLift-Android/
├── app/
│   ├── build.gradle.kts                  — App-level Gradle config
│   ├── proguard-rules.pro                — ProGuard rules for release
│   └── src/main/
│       ├── AndroidManifest.xml           — App manifest
│       ├── java/com/dailylift/app/
│       │   ├── DailyLiftApplication.kt   — Application class (WorkManager init)
│       │   ├── MainActivity.kt           — Main entry + Compose navigation
│       │   ├── data/
│       │   │   └── QuotesData.kt         — 375+ real quotes across 6 categories
│       │   ├── model/
│       │   │   └── Quote.kt              — Quote + QuoteCategory data classes
│       │   ├── ui/
│       │   │   ├── QuoteViewModel.kt     — MVVM ViewModel (favorites, filter, daily)
│       │   │   ├── components/
│       │   │   │   └── QuoteCardView.kt  — Reusable card with favorite + share
│       │   │   ├── navigation/
│       │   │   │   └── Navigation.kt     — Bottom nav tab definitions
│       │   │   ├── screens/
│       │   │   │   ├── TodayScreen.kt    — Hero animated gradient quote screen
│       │   │   │   ├── BrowseScreen.kt   — Filterable, searchable quote list
│       │   │   │   ├── FavoritesScreen.kt— Saved quotes with swipe-to-delete
│       │   │   │   └── SettingsScreen.kt — Notifications, theme, about
│       │   │   └── theme/
│       │   │       ├── Color.kt          — Brand color palette
│       │   │       ├── Theme.kt          — Material 3 theme setup
│       │   │       └── Type.kt           — Typography (serif for quotes)
│       │   ├── utils/
│       │   │   ├── NotificationHelper.kt — Daily notification scheduling (WorkManager)
│       │   │   └── BootReceiver.kt       — Reschedule notifications on reboot
│       │   └── widget/
│       │       └── DailyLiftWidget.kt    — Glance home screen widget
│       └── res/
│           ├── drawable/                 — Icons and vector assets
│           ├── layout/                   — Widget loading layout
│           ├── mipmap-anydpi-v26/        — Adaptive icon
│           ├── values/                   — Strings, colors, themes
│           └── xml/                      — Widget provider info
├── build.gradle.kts                      — Project-level Gradle config
├── settings.gradle.kts                   — Gradle settings
├── gradle.properties                     — Gradle properties
└── gradle/wrapper/
    └── gradle-wrapper.properties         — Gradle wrapper config
```

---

## Opening in Android Studio

1. Clone or download this repository
2. Open Android Studio and select **Open an Existing Project**
3. Navigate to the `DailyLift-Android/` directory and click **Open**
4. Wait for Gradle sync to complete
5. Select a device/emulator and press **Run** (▶)

---

## Features

### Today Screen
- Full-screen animated gradient background — color changes per quote category
- Large serif quote text, centered with generous whitespace
- Author attribution below the quote
- Animated heart favorite button with spring bounce
- Share button (native Android share sheet)
- Copy to clipboard button
- "Peek at tomorrow" preview (tap to reveal next day's quote)
- Soft floating orb animations with blur effects

### Browse
- Horizontal category filter pills (All / Success / Love / Life / Hustle / Mindset / Happiness)
- Vertical scrolling list of all 375+ quotes
- Real-time search by quote text or author name
- Collapsing toolbar with large title

### Favorites
- All saved quotes in one place
- Swipe-to-delete individual favorites with red background
- Clear all button with confirmation dialog
- Beautiful empty state with gradient heart icon

### Settings
- Daily notification toggle + time picker (Material 3)
- Theme: System / Light / Dark (SharedPreferences backed)
- Rate app (Play Store link)
- Share app
- Privacy policy link
- App version display

### Home Screen Widget (Glance)
- Medium widget with date, category label, quote, and author
- Category-colored background
- Taps open the main app
- Updates daily

---

## Daily Quote Logic

Quotes are selected deterministically using the day-of-year:

```kotlin
val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
val index = (dayOfYear - 1) % allQuotes.size
return allQuotes[index]
```

- Same quote for all users on a given day
- Cycles through all 375+ quotes annually
- Widget and main app always show the same quote

---

## Quote Categories (375+ total)

| Category   | Count | Gradient          |
|------------|-------|-------------------|
| Success    | 66    | Warm peach-orange |
| Love       | 63    | Soft pink-rose    |
| Life       | 63    | Mint-teal         |
| Hustle     | 62    | Purple-indigo     |
| Mindset    | 62    | Blue-sky          |
| Happiness  | 63    | Warm yellow       |

Authors include: Marcus Aurelius, Steve Jobs, Maya Angelou, Rumi, Buddha, Martin Luther King Jr., Dalai Lama, Oprah Winfrey, Albert Einstein, Tony Robbins, Brené Brown, Viktor Frankl, and many more.

---

## Architecture

- **MVVM** — `QuoteViewModel` manages quotes, favorites, filtering via `StateFlow`
- **Jetpack Compose** — 100% Compose UI, no XML layouts (except widget loading)
- **Material 3** — Full Material You design system
- **Persistence** — `SharedPreferences` for favorites and settings
- **Notifications** — `WorkManager` for reliable daily scheduling
- **Widget** — Jetpack Glance for modern widget implementation
- **No external dependencies** — Only official AndroidX and Google libraries

---

## Customization

### Adding More Quotes
Open `app/src/main/java/com/dailylift/app/data/QuotesData.kt` and add entries:

```kotlin
Quote(text = "Your quote here.", author = "Author Name", category = QuoteCategory.MINDSET),
```

### Changing Gradient Colors
Each category's gradient is defined in `Quote.kt` inside `QuoteCategory`:

```kotlin
SUCCESS(
    gradientColors = listOf(Color(0xFFFFD68F), Color(0xFFFFA666)),
    accentColor = Color(0xFFE68C33)
),
```

### App Icon
Replace the vector drawables in `res/drawable/ic_launcher_foreground.xml` and `ic_launcher_background.xml`, or add raster PNGs to the `mipmap-*` folders.

---

## Play Store Submission Checklist

- [ ] Add final app icon (replace vector drawable or use Image Asset Studio)
- [ ] Set your signing key in `build.gradle.kts` for release builds
- [ ] Update `applicationId` to your own (e.g., `com.yourcompany.dailylift`)
- [ ] Test on real device (notifications require physical hardware)
- [ ] Verify widget appears in widget gallery
- [ ] Add Play Store screenshots (phone and tablet)
- [ ] Write Play Store description and choose category (Lifestyle)
- [ ] Set content rating (likely Everyone)
- [ ] Generate signed APK/AAB → Upload to Google Play Console

---

## License

MIT License — free to use, modify, and distribute.

---

*Built with Perplexity Computer*
