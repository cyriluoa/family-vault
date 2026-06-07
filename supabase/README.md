# FamilyVault Supabase

Supabase provides Auth, Postgres, Row Level Security, Storage, migrations, and optional Edge Functions.

Directory layout:

```text
migrations/  Database migrations.
functions/   Optional Supabase Edge Functions.
seed.sql     Local development seed data.
```

Keep secrets out of this directory. Use local environment files that are ignored by Git.
