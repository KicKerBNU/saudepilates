# Firebase API keys — security and rotation

Firebase client API keys were previously committed in this repository. **Rotate them now** even after removing them from git history.

## Important context

Firebase API keys in mobile and web clients are not fully secret (they ship inside apps). Real protection comes from:

- **Firestore / Storage security rules**
- **API key restrictions** in [Google Cloud Console → Credentials](https://console.cloud.google.com/apis/credentials?project=saudepilates-170df)
- **Firebase App Check** (recommended for production)

Still, keys in a public git repo should be **rotated** and **restricted**.

## Rotate keys (do this in Google Cloud / Firebase)

1. Open [Google Cloud Console → Credentials](https://console.cloud.google.com/apis/credentials?project=saudepilates-170df).
2. For each exposed key (iOS, Android, Browser):
   - Click the key → **Edit**
   - Under **Application restrictions**, set:
     - **iOS**: bundle ID `com.saudepilates.app`
     - **Android**: package `com.saudepilates.app` + SHA-1 of your signing cert
     - **Web**: HTTP referrers (`saudepilates.com.br/*`, `localhost:*`, your hosting domain)
   - Under **API restrictions**, limit to Firebase-related APIs only.
3. **Regenerate** or **create a new key** and disable/delete the old one.
4. Re-download config files from [Firebase Console → Project settings](https://console.firebase.google.com/project/saudepilates-170df/settings/general):
   - iOS → `GoogleService-Info.plist`
   - Android → `google-services.json`
   - Web → copy config into `.env` (see `.env.example`)

## Local setup (never commit real files)

| Platform | Template | Local file (gitignored) |
|----------|----------|-------------------------|
| iOS | `ios/SaudePilates/GoogleService-Info.plist.example` | `ios/SaudePilates/GoogleService-Info.plist` |
| Android | `android/app/google-services.json.example` | `android/app/google-services.json` |
| Web | `.env.example` | `.env` |

```bash
# iOS
cp ios/SaudePilates/GoogleService-Info.plist.example ios/SaudePilates/GoogleService-Info.plist
# Then replace with the file downloaded from Firebase Console

# Android
cp android/app/google-services.json.example android/app/google-services.json
# Then replace with the file downloaded from Firebase Console

# Web (local dev)
cp .env.example .env
# Fill VITE_FIREBASE_* from Firebase Console

# Web (production build / firebase deploy)
cp .env.example .env.production
# Same VITE_FIREBASE_* values — Vite reads .env.production when NODE_ENV=production
```

## Production deploy (Firebase Hosting)

`yarn build` embeds `VITE_*` at compile time. Before deploying:

1. Create `.env.production` locally (gitignored) with your `VITE_FIREBASE_*` values.
2. Run `yarn build && firebase deploy --only hosting`.

Set these in CI/CD as environment variables if you automate deploys.

## If keys were pushed to GitHub

Rotating keys in Google Cloud is required. Optionally purge history with `git filter-repo` or GitHub secret scanning remediation — rotating makes old keys useless.
