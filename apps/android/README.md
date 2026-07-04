# FamilyVault Android

Android is the first FamilyVault client and is built with Kotlin, Jetpack Compose, Hilt, Navigation Compose, and Supabase Kotlin.

## Current App Slice

Implemented so far:

- Compose app shell and route graph.
- FamilyVault theme tokens for a calm, secure, document-focused UI.
- Google sign-in through Supabase Auth.
- Android deep-link callback handling for `com.familyvault.app://auth-callback`.
- Session observation so login/logout changes route the app automatically.
- Signed-in overflow menu with Profile and Sign out.
- Profile screen that reads the app profile from `public.profiles` and account metadata from Supabase Auth.

Expected current routing:

```text
No session -> Auth
Signed in -> Onboarding
Profile menu item -> Profile
Sign out -> Auth
```

Vault membership lookup is not wired yet, so signed-in users always start at onboarding for now.

## Local Config

Copy `local.properties.example` into `local.properties` and keep Android Studio's existing `sdk.dir` line if present.

Required FamilyVault keys:

```properties
familyvault.supabase.url=https://YOUR_PROJECT_REF.supabase.co
familyvault.supabase.publishableKey=YOUR_SUPABASE_PUBLISHABLE_OR_ANON_KEY
familyvault.auth.redirectUri=com.familyvault.app://auth-callback
```

`local.properties` is ignored by Git. Do not commit service-role keys, Google client secrets, signing keys, or real family documents.

## Auth Setup

Supabase Auth is the app's auth system. Google is configured as a Supabase provider.

Dashboard setup required:

- Supabase Google provider enabled with Google OAuth client ID/secret.
- Supabase redirect URL allow-list includes `com.familyvault.app://auth-callback`.
- Android manifest includes the matching custom-scheme callback intent filter.

For MVP/dev this uses a custom scheme. Before wider production release, replace or supplement it with verified Android App Links using an owned HTTPS domain.

## Compose Notes

Compose screens live in Kotlin source files instead of XML layout files under `res/layout`.

The screen layer should stay mostly display-focused. ViewModels own screen state, repositories own Supabase/data access, and Hilt wires dependencies.