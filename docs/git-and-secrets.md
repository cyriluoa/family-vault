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

## Before Pushing

Run:

```powershell
git status --short
rg -n --hidden --glob '!**/.git/**' --glob '!node_modules/**' "(?i)(secret|service_role|password|token|api[_-]?key|private[_-]?key|credential)" .
```

Review anything the search finds. Placeholder names in `.env.example` are expected; real values are not.
