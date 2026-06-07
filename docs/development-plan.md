# Development Plan

FamilyVault should be built as a thin working product slice before adding broad features. The first milestone should prove that a family can save and retrieve real documents safely.

## Recommended Build Order

1. Supabase foundation.
2. Android auth and vault onboarding.
3. Android share-sheet import.
4. Cloud upload and metadata save.
5. Document list, filters, and external open.
6. Manual versioning.
7. Expiry reminders and activity log.
8. Invite links/codes.

## Supabase First, But Not All Of Supabase

Build the core Supabase pieces first:

- Auth.
- Initial Postgres schema.
- Row Level Security policies.
- One private document storage bucket.
- Vault-based storage paths.
- Seed data for local development.

Avoid building the full backend, worker, OCR, AI, or web app before the Android upload flow works. The best early proof is: create vault, create Family subject, receive a PDF from Android share sheet, add metadata, upload to cloud storage, list it, and open it externally.

## First Useful Vertical Slice

The first end-to-end slice should support:

- Sign in.
- Create or join one default vault.
- Auto-create the vault's Family subject.
- Receive a shared file on Android.
- Complete required metadata, including primary subject.
- Save document metadata.
- Upload the file to private Supabase Storage.
- Show the document in a list.
- Open the current version in an external Android app.

## Keep The MVP Small

Do not build custom document type management, OCR, AI classification, semantic search, public links, complex multi-vault switching, or per-document private permissions in the first pass. Keep the schema flexible enough for those features, but make the first version boring, reliable, and trusted.
