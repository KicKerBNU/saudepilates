# Saúde Pilates

Management platform for Pilates studios — students, professors, payments, schedules, and anamnesis. One Firebase backend (`saudepilates-170df`) powers all clients.

**Production web:** [saudepilates.com.br](https://saudepilates.com.br)  
**Privacy policy:** [saudepilates.com.br/privacy](https://saudepilates.com.br/privacy)

---

## Applications

| Platform | Stack | Folder | Package / ID | Docs |
|----------|-------|--------|--------------|------|
| **Web** | Vue 3 + Vite + Firebase | `src/` | — | Run locally below |
| **iOS** | SwiftUI + Firebase | `ios/` | `com.saudepilates.app` | [ios/README.md](ios/README.md) |
| **Android** | Kotlin + Jetpack Compose + Firebase | `android/` | `com.saudepilates.app` | [android/README.md](android/README.md) |

All native apps share the same Firebase project, roles (Admin, Professor, Student), and Firestore collections.

---

## Web app (Vue)

### Requirements

- Node.js 18+
- Yarn

### Run locally

```bash
yarn install
cp .env.example .env   # add VITE_FIREBASE_* from Firebase Console
yarn dev
```

### Build

Requires `.env` with `VITE_FIREBASE_*` (same as local dev).

```bash
yarn build
```

---

## Native apps — quick start

### iOS

```bash
cd ios
xcodegen generate
open SaudePilates.xcodeproj
```

Replace `ios/SaudePilates/GoogleService-Info.plist` with the file from [Firebase Console](https://console.firebase.google.com/project/saudepilates-170df/settings/general). See [ios/README.md](ios/README.md) for full setup and App Store notes.

### Android

Open the **`android/`** folder in Android Studio (not the repo root). Replace `android/app/google-services.json` from Firebase Console.

```bash
cd android
./gradlew assembleDebug
```

See [android/README.md](android/README.md) for emulator/device testing and **Google Play Store** publishing.

---

## Firebase setup (all platforms)

1. Open [Firebase Console → saudepilates-170df](https://console.firebase.google.com/project/saudepilates-170df/settings/general)
2. Register each platform with package/bundle ID **`com.saudepilates.app`**
3. Download config files (templates are in the repo; real files stay local and are **gitignored**):
   - **Web** — copy `.env.example` → `.env` and set `VITE_FIREBASE_*`
   - **iOS** — `GoogleService-Info.plist` → `ios/SaudePilates/`
   - **Android** — `google-services.json` → `android/app/`

Enable **Email/Password** authentication in Firebase Console.

**API key rotation:** If keys were committed to git, rotate them immediately. See [docs/FIREBASE-SECRETS.md](docs/FIREBASE-SECRETS.md).

---

## Feature parity (native apps)

| Area | Admin | Professor | Student |
|------|-------|-----------|---------|
| Auth | Login, register company | Login | Login |
| Dashboard | Stats, quick actions | Stats | Plan, next class |
| People | Students, professors, plans | My students | — |
| Payments | Register, monthly, commissions | Earnings | History |
| Schedule | Full agenda | My schedule | My classes |
| Other | Anamnesis, settings | Attendance, evolution, messages, anamnesis | — |

iOS has a few extra screens (payment charts, subscription gate UI). See platform READMEs for the full table.

---

## Publishing status

| Platform | Status |
|----------|--------|
| Web | Live at saudepilates.com.br |
| iOS | Xcode ready — **App Store guide** in [ios/README.md#app-store](ios/README.md#app-store) |
| Android | Android Studio — **Play Console setup in progress** — [android/README.md#google-play-store](android/README.md#google-play-store) |

---

## Repository structure

```
saudepilates/
├── src/                 # Vue web app
├── ios/                 # Native iOS (SwiftUI)
├── android/             # Native Android (Compose)
├── public/
└── package.json
```

---

## Contact

`saudepilatess@gmail.com`
