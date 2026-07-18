# FamilyVault

FamilyVault is a private family document management app for replacing messy document sharing in WhatsApp and similar chat apps. It gives families a secure shared vault where members can upload, organize, search, version, and manage important documents.

The project is Android-first, with a future web app and backend services designed to grow around Supabase.

## Monorepo Layout

```text
apps/
  android/       Android app, built first with Kotlin.
  web/           Future React/Next.js web app.
services/
  api/           Future Node + Hono API, likely deployed to Cloud Run.
  worker/        Future background worker for OCR, AI, search, and processing jobs.
supabase/
  migrations/    Database schema migrations.
  functions/     Optional Supabase Edge Functions.
  seed.sql       Local development seed data.
packages/
  shared/        Shared types, constants, and validation helpers.
docs/
  android-navigation.md
  architecture.md
  database.md
  design-system.md
  development-plan.md
  git-and-secrets.md
  product-notes.md
```

## Current Android Status

The Android app is a Jetpack Compose app with Hilt, Navigation Compose, Supabase Auth/PostgREST/Storage clients, and a lightweight design system. Current implemented flow:

- Google sign-in through Supabase Auth using Android custom tabs and `com.familyvault.app://auth-callback`.
- Email sign-in links through Supabase Auth, with Google recommended during development because the built-in email sender is rate-limited.
- Supabase session observation for signed-in/signed-out routing plus vault-aware signed-in startup.
- Signed-in app shell with a profile avatar and overflow sign-out menu.
- No-vault onboarding with Create your first vault and Join a vault actions.
- Create Vault form wired to the `public.create_vault` Supabase RPC.
- Vaults screen wired to the `public.my_vaults` Supabase RPC.
- Profile screen that combines `public.profiles` data with Supabase Auth account metadata.
- Shared scaffold header pattern for modern screen titles without a persistent top bar.
- Local Android config through ignored `apps/android/local.properties`, documented by `apps/android/local.properties.example`.

The app now checks active vault memberships after sign-in. Users with no vaults see onboarding, while users with active vault memberships start on the Vaults screen. Creating a vault routes to Vaults after the RPC succeeds.

## MVP Features

- Family spaces/vaults for shared household or family groups.
- Data-model support for multiple vaults, with the MVP Android UI optimized around one default vault.
- Invite links/codes and vault membership.
- Android share-sheet upload from WhatsApp, Gmail, Files, Google Drive, Adobe Reader, and similar apps.
- Upload PDFs, images, and documents into cloud storage managed by FamilyVault.
- Metadata-first organization instead of folder-first organization.
- Subject-centric document organization for people, properties, vehicles, trips, organizations, and family-wide entities.
- A default Family subject for family-wide documents.
- Document categories, types, tags, and metadata.
- Fixed starter taxonomy for document categories and types.
- Simple active/inactive document status.
- Simple document versioning with one current version and older versions.
- Latest-version tracking.
- Search and filtering.
- Expiry reminders.
- Activity log.
- External file opening on Android.
- Strong backend authentication, authorization, permissions, and Row Level Security.

## Future Features

- Restricted or private document access inside a vault.
- OCR and text extraction.
- Automatic document classification.
- Metadata extraction for people, document type, expiry dates, and related subjects.
- Semantic search and natural-language search.
- Missing-document detection.
- Emergency or travel mode.
- Document summaries.
- Action-required workflows.
- Timeline view.
- Web app access.
- Public document links, if needed later.
- Email-based invites.
- Custom document type management.

## Architecture

The Android app is the first client. It will talk to Supabase and backend services for authentication, permissions, metadata, uploads, downloads, and document workflows.

Supabase provides Auth, Postgres, Row Level Security, Storage, and migrations. A Cloud Run API using Node and Hono can hold business logic that should not live directly in the client. Supabase Edge Functions may be used for lightweight app logic when they are a good fit.

Files are stored in cloud storage, not permanently on-device. The Android app may temporarily download or cache a file when opening it externally or sharing it through Android's native share sheet. MVP sharing should send files as attachments, not public links.

Documents should use one private Supabase Storage bucket with vault-based paths and policies. Separate environments should use separate Supabase projects or environment configuration.

Future heavy processing, such as OCR, AI classification, embeddings, semantic search, and smart reminders, should run in a worker service.

Security principle: never trust the client. Permissions must be enforced server-side and with Supabase Row Level Security.
