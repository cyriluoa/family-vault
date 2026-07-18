# Architecture

FamilyVault is an Android-first monorepo designed around Supabase, cloud storage, and a backend API that can grow over time.

## Clients

The Android app talks to the backend and Supabase for authentication, document metadata, permissions, upload flows, downloads, and activity tracking. Android share-sheet integration is a key MVP feature for receiving files from other apps and sharing vault files back out as attachments.

The data model should support multiple vaults from day one. The MVP Android UI should still optimize around one default vault and avoid overbuilding vault switching.

A future web app will be hosted separately, likely on Cloudflare Pages. It should use the same auth, permissions, and backend data model as Android.

## Backend

Supabase handles:

- Auth.
- Postgres.
- Row Level Security.
- Storage.
- Migrations.

A Cloud Run API can hold business logic that should not live in the client. The API is likely to use Node and Hono. It can coordinate invite flows, document lifecycle operations, permissions-sensitive writes, upload preparation, and other application logic.

Supabase Edge Functions may be used for lightweight app logic where they are simpler than a full API route.

## Auth Flow

Supabase Auth is the source of truth for FamilyVault accounts. Google sign-in and email sign-in are login methods for the same FamilyVault account, not separate account systems.

When a user signs in with Google for the first time, Supabase creates an `auth.users` row. The database trigger then creates one matching `public.profiles` row. Later sign-ins should reuse the same Supabase user/profile.

If a user signs in with Google using an email address and later uses Continue with email for that same verified email address, the intended behavior is that they land in the same FamilyVault profile. This should be tested early with the real Supabase project to confirm provider linking behaves as expected.

For Android MVP, start with Supabase OAuth plus Android deep links. The Continue with Google button should launch the Google OAuth flow through Supabase, return to the app, and store a Supabase session. Continue with email currently uses Supabase email sign-in links. The default Supabase email sender is rate-limited, so Google is the recommended development sign-in path until custom SMTP is configured.

The MVP Android redirect can use a custom scheme such as `com.familyvault.app://auth-callback` for speed of development. Before wider production release, replace or supplement this with verified Android App Links using an HTTPS domain owned by FamilyVault, such as `https://auth.familyvault.app/callback`, so Android can verify the app owns the callback domain.

### Android Auth Implementation

The Android app uses a shared `SupabaseClient` provided by Hilt. The client installs Auth, PostgREST, and Storage plugins.

Current Android auth flow:

1. `AuthScreen` sends button actions to `AuthViewModel`.
2. `AuthRepositoryImpl` starts Google OAuth with Supabase Auth.
3. Supabase opens Google sign-in in a custom tab.
4. Supabase redirects back to `com.familyvault.app://auth-callback`.
5. `MainActivity` receives the deep link and calls `supabaseClient.handleDeeplinks(intent)`.
6. Supabase imports the session and updates `sessionStatus`.
7. `AppViewModel` observes `sessionStatus` and maps it to app routing state.

The signed-in app shell checks vault membership before choosing its start route. The shell shows a profile avatar for profile access and keeps an overflow menu for secondary actions. Profile data is split into two concepts:

- `public.profiles`: FamilyVault app profile fields such as display name, email, avatar URL, and default vault preference.
- Supabase Auth account metadata: auth user id, providers, phone, account creation, last sign-in, and email confirmation.
Create-vault is handled through the public.create_vault Postgres RPC. Android sends only the vault name and creator person name; the RPC uses `auth.uid()` from the verified Supabase session to create the vault, default Family subject, creator person subject, owner membership, default vault preference, and activity log in one transaction.

Post-login routing:

- If the user has active vault membership, start on the Vaults screen for now. Later this can open the default vault directly.
- If the user has no vault yet, show onboarding to create or join a vault.
- If the user came from an invite link or code, accept the invite, create/link their person subject in that vault, and then enter the vault.

## Worker

A future worker service can handle heavier asynchronous jobs:

- OCR.
- AI classification.
- Metadata extraction.
- Smart file optimization analysis and derivative generation.
- Embedding generation.
- Semantic search indexing.
- Reminder processing.
- Other smart document features.

## Files

Files are stored in cloud storage, not permanently on-device. Metadata and version records live in Postgres. The Android app may temporarily download or cache files locally when opening them externally or sharing them through Android's native share sheet.

Use one private Supabase Storage bucket for documents, with vault-based object paths and storage policies. Separate environments should be handled with separate Supabase projects or environment configuration, not separate document-class buckets.

In the MVP, external sharing should send the actual file attachment, such as a PDF, image, or document. Public document links are a future feature, not an MVP requirement.

Original files should remain the source of truth. Future workers may create optimized derivatives, previews, thumbnails, or suggested compressed copies, but destructive compression should not be automatic in the MVP.

## Security

Never trust the client. Authentication, authorization, and vault permissions must be enforced server-side and with Supabase Row Level Security.

Vault members can access documents in their vault by default for the MVP. More restrictive per-document access can be added later without changing the core idea that vault membership is the default permission boundary.

Invite links and codes are the first invite mechanism so families can share access through WhatsApp. Email-based invites can be added later.

Post-MVP security hardening should include verified Android App Links for auth callbacks instead of relying only on custom URL schemes. Custom schemes are convenient for development but are not ownership-verified in the same way as HTTPS App Links.
