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

## Worker

A future worker service can handle heavier asynchronous jobs:

- OCR.
- AI classification.
- Metadata extraction.
- Embedding generation.
- Semantic search indexing.
- Reminder processing.
- Other smart document features.

## Files

Files are stored in cloud storage, not permanently on-device. Metadata and version records live in Postgres. The Android app may temporarily download or cache files locally when opening them externally or sharing them through Android's native share sheet.

Use one private Supabase Storage bucket for documents, with vault-based object paths and storage policies. Separate environments should be handled with separate Supabase projects or environment configuration, not separate document-class buckets.

In the MVP, external sharing should send the actual file attachment, such as a PDF, image, or document. Public document links are a future feature, not an MVP requirement.

## Security

Never trust the client. Authentication, authorization, and vault permissions must be enforced server-side and with Supabase Row Level Security.

Vault members can access documents in their vault by default for the MVP. More restrictive per-document access can be added later without changing the core idea that vault membership is the default permission boundary.

Invite links and codes are the first invite mechanism so families can share access through WhatsApp. Email-based invites can be added later.
