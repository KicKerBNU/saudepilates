# Saúde Pilates — iOS (Native Swift)

Native SwiftUI app mirroring the web application features, connected to the same Firebase project (`saudepilates-170df`).

## Requirements

- macOS with **Xcode 15+**
- iOS 16+ device or simulator
- Firebase iOS app registered in [Firebase Console](https://console.firebase.google.com/)

## Setup

### 1. Register iOS app in Firebase (required)

The app will show a setup screen until Firebase is configured.

1. Open [Firebase Console → saudepilates-170df](https://console.firebase.google.com/project/saudepilates-170df/settings/general)
2. Click **Add app** → **iOS**
3. Bundle ID: `com.saudepilates.app`
4. Download `GoogleService-Info.plist`
5. Replace `ios/SaudePilates/GoogleService-Info.plist` with the downloaded file
6. In Xcode: **Product → Clean Build Folder**, then run again

**Alternative:** paste the `GOOGLE_APP_ID` from Firebase into `SaudePilates/Utilities/FirebaseConfiguration.swift` (`iosGoogleAppID`).

> The template plist uses a placeholder `GOOGLE_APP_ID`. Firebase rejects it and the app used to crash — now it shows setup instructions instead.

### 2. Generate Xcode project

```bash
cd ios
xcodegen generate
open SaudePilates.xcodeproj
```

### 3. Configure signing

In Xcode → Target **SaudePilates** → **Signing & Capabilities**:

- Select your Team
- Ensure Bundle Identifier is `com.saudepilates.app` (or your own, matching Firebase)

### 4. Run

Select an iPhone simulator or device and press **Run** (⌘R).

## Feature parity

### Authentication
- Login / Register company (admin)
- Role-based routing: Admin, Professor, Student
- Subscription gate for admin (trial / expiration check)

### Admin
| Feature | Screen |
|---------|--------|
| Dashboard stats | Admin Dashboard |
| Students CRUD, deactivate/reactivate | Students |
| Student payment history + delete | Tap student |
| Professors CRUD, deactivate/reactivate | Professors |
| Plans management | Plans |
| Register payment | Payment Registration |
| Monthly payments + delete | Monthly Payments |
| Payment charts | Visualization |
| Professor commissions + mark paid | Professor Payments |
| Schedule management | Agenda |
| Anamnesis | Anamnese |
| Company settings | Configurações |
| Subscription info | Assinatura |

### Professor
| Feature | Screen |
|---------|--------|
| Dashboard | Início |
| My students | Alunos |
| Schedule | Agenda |
| Earnings history | Ganhos |
| Attendance control | Presença |
| Student evolution | Evolução |
| Messages | Mensagens |
| Anamnesis | Anamnese |

### Student
| Feature | Screen |
|---------|--------|
| Dashboard | Início |
| Payment history | Pagamentos |
| Upcoming classes | Agenda |

## Architecture

```
ios/SaudePilates/
├── Models/          # Data types matching Firestore
├── Services/        # Firebase Auth + Firestore operations
├── Utilities/       # Currency, dates, toasts
├── Views/
│   ├── Auth/        # Login, Register
│   ├── Admin/       # All admin screens
│   ├── Professor/   # Professor screens
│   ├── Student/     # Student screens
│   └── Shared/      # Toast, loading, dialogs
└── SaudePilatesApp.swift
```

## Firestore collections used

- `users`, `companies`, `plans`
- `studentPayments`, `professorPayments`, `professorPayouts`
- `scheduledClasses`, `attendanceRecords`, `evolutions`
- `anamnesis`, `messages`, `subscriptions`

## Notes

- Payment delete cascades to linked professor commission (same logic as web)
- Professor payments list shows only active commissions linked to existing student payments
- Admin "Total Students" counts active students only
- Stripe subscription payment opens web portal link (native Stripe can be added later)

## Troubleshooting

**Firebase Auth error on register user (admin creating student/professor):**
Ensure Email/Password auth is enabled in Firebase Console.

**Missing GoogleService-Info.plist / invalid GOOGLE_APP_ID:**
Download the real plist from Firebase Console for your iOS app.

**Build fails resolving Firebase packages:**
Xcode → File → Packages → Reset Package Caches, then build again.
