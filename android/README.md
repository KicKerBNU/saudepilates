# Saúde Pilates — Android (Native Kotlin)

Native Jetpack Compose app mirroring the web and iOS application features, connected to the same Firebase project (`saudepilates-170df`).

## Requirements

- **Android Studio Ladybug (2024.2.1)** or newer
- **JDK 17**
- Android 8.0+ device or emulator (API 26+)
- Firebase Android app registered in [Firebase Console](https://console.firebase.google.com/)

## Setup

### 1. Register Android app in Firebase (required)

The app shows a setup screen until Firebase is configured with a valid app ID.

1. Open [Firebase Console → saudepilates-170df](https://console.firebase.google.com/project/saudepilates-170df/settings/general)
2. Click **Add app** → **Android**
3. Package name: `com.saudepilates.app`
4. Download `google-services.json`
5. Replace `android/app/google-services.json` with the downloaded file
6. In Android Studio: **Build → Clean Project**, then run again

> The template `google-services.json` uses a placeholder `mobilesdk_app_id`. The app detects this and shows setup instructions instead of crashing.

### 2. Configure Android SDK

**With Android Studio (recommended):** Open the `android/` folder in Android Studio — it creates `local.properties` automatically.

**Command line only:** Install the SDK and point Gradle at it:

```bash
brew install --cask android-commandlinetools
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

Create `android/local.properties` (this file is gitignored):

```properties
sdk.dir=/opt/homebrew/share/android-commandlinetools
```

On Intel Macs, the Homebrew path may be `/usr/local/share/android-commandlinetools`.

### 3. Build and run

```bash
cd android
./gradlew assembleDebug
```

Or open in Android Studio and press **Run**.

### 4. Install on device/emulator

```bash
./gradlew installDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Feature parity

### Authentication
- Login / Register company (admin)
- Role-based routing: Admin, Professor, Student

### Admin
| Feature | Screen |
|---------|--------|
| Dashboard stats (active students) | Início |
| Students list, create, edit, payment history + delete | Pessoas → Alunos |
| Professors list | Pessoas → Professores |
| Plans list | Pessoas → Planos |
| Register payment (with commission) | Pagamentos → Registrar |
| Monthly payments + delete | Pagamentos → Mês |
| Professor commissions | Pagamentos → Professores |
| Schedule | Mais → Agenda |
| Anamnesis | Mais → Anamnese |
| Company settings | Mais → Configurações |

### Professor
| Feature | Screen |
|---------|--------|
| Dashboard | Início |
| My students | Alunos |
| Schedule | Agenda |
| Earnings history | Mais → Ganhos |
| Attendance | Mais → Presença |
| Student evolution | Mais → Evolução |
| Messages | Mais → Mensagens |
| Anamnesis | Mais → Anamnese |

### Student
| Feature | Screen |
|---------|--------|
| Dashboard (plan, next class) | Início |
| Payment history | Pagamentos |
| Schedule | Agenda |

## Project structure

```
android/
├── app/
│   ├── google-services.json      # Firebase config (replace from console)
│   └── src/main/kotlin/com/saudepilates/app/
│       ├── data/models/          # Shared data models
│       ├── data/repositories/    # Firebase Auth + Firestore
│       ├── ui/admin/             # Admin screens
│       ├── ui/professor/         # Professor screens
│       ├── ui/student/           # Student screens
│       ├── ui/auth/              # Login / Register
│       └── ui/navigation/        # Role-based bottom navigation
├── build.gradle.kts
└── settings.gradle.kts
```

## Shared Firebase collections

Same as web/iOS: `users`, `companies`, `plans`, `studentPayments`, `professorPayments`, `professorPayouts`, `scheduledClasses`, `attendanceRecords`, `evolutions`, `anamnesis`, `messages`, `subscriptions`.

## Notes

- Admin user creation uses a secondary Firebase Auth instance so the admin session stays active (same pattern as iOS).
- Deleting a student payment cascades to the linked professor commission record.
- Professor commission list only shows payments linked to existing student payments.
