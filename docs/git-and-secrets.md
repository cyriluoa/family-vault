# Git And Secrets

This repo should be safe to push when only placeholder example files are committed.

## Commit

- Source code.
- Documentation.
- Migrations.
- Seed files without real personal data.
- Example environment files such as `.env.example`.
- Placeholder README files.

## Do Not Commit

- `.env` or `.env.*` files.
- Supabase service role keys.
- Android `local.properties`.
- Android signing files, including `.jks`, `.keystore`, and `key.properties`.
- Google or cloud service account JSON files.
- Real family documents.
- Production database dumps.
- Personal invite codes or test data tied to real people.

## Environment Files

Use `.env.example` to document required variables. Put real values in local files such as `.env`, `.env.local`, or platform-specific secret stores. Those files are ignored by Git.

The Supabase anon key can be used in clients when Row Level Security is correct. The Supabase service role key is server-only and must never be included in Android or web builds.

## Android Local Config

Android client config should live in ignored `apps/android/local.properties`:

```properties
familyvault.supabase.url=https://YOUR_PROJECT_REF.supabase.co
familyvault.supabase.publishableKey=YOUR_SUPABASE_PUBLISHABLE_OR_ANON_KEY
familyvault.auth.redirectUri=com.familyvault.app://auth-callback
```

Commit `apps/android/local.properties.example`, but never commit the real `local.properties` file. The Supabase publishable/anon key is allowed in clients when RLS is correct, but service role keys and Google OAuth client secrets are server/dashboard-only.

## Before Pushing

Run:

```powershell
git status --short
rg -n --hidden --glob '!**/.git/**' --glob '!node_modules/**' "(?i)(secret|service_role|password|token|api[_-]?key|private[_-]?key|credential)" .
```

Review anything the search finds. Placeholder names in `.env.example` are expected; real values are not.
