# FamilyVault Android

Android is the first FamilyVault client and is built with Kotlin, Jetpack Compose, Hilt, Navigation Compose, and Supabase Kotlin.

## Current App Slice

Implemented so far:

- Compose app shell and route graph.
- FamilyVault theme tokens for a calm, secure, document-focused UI.
- Google sign-in through Supabase Auth.
- Email sign-in links through Supabase Auth. Google is recommended during development because Supabase's built-in email sender is rate-limited.
- Android deep-link callback handling for `com.familyvault.app://auth-callback`.
- Session observation so login/logout changes route the app automatically.
- Signed-in shell with profile avatar access and an overflow menu for sign out.
- No-vault onboarding with Create your first vault and Join a vault actions.
- Create Vault form wired to the `public.create_vault` Supabase RPC.
- Vaults screen wired to the `public.my_vaults` Supabase RPC, using a compact vault list and bottom-right add action.
- Shared scaffold header support for screen title/subtitle areas without a persistent top app bar.
- Profile screen that reads the app profile from `public.profiles` and account metadata from Supabase Auth.

Expected current routing:

```text
No session -> Auth
Signed in + no active vault memberships -> Onboarding
Signed in + active vault memberships -> Vaults
Create your first vault -> Create Vault
Create vault success -> Vaults
Vault selection -> Documents
Profile avatar -> Profile
Sign out -> Auth
```

Signed-in startup calls the `public.my_vaults` RPC to decide whether to show onboarding or the Vaults screen. Creating a vault uses one RPC so the vault, default Family subject, creator person subject, owner membership, default vault preference, and activity log are created together. The Vaults screen uses the same vault-list RPC to show active vault memberships.

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
