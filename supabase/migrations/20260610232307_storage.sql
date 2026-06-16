-- FamilyVault private document storage.
-- Uses one private bucket and vault-based paths:
-- vaults/{vault_id}/documents/{document_id}/versions/{version_id}/{filename}

insert into storage.buckets (id, name, public, file_size_limit)
values ('documents', 'documents', false, null)
on conflict (id) do update
set public = excluded.public,
    file_size_limit = excluded.file_size_limit;

create or replace function public.storage_object_vault_id(object_name text)
returns uuid
language sql
immutable
as $$
  select case
    when split_part(object_name, '/', 1) = 'vaults'
      and split_part(object_name, '/', 2) ~* '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
    then split_part(object_name, '/', 2)::uuid
    else null
  end;
$$;

create policy "Vault members can read document storage objects"
  on storage.objects for select
  to authenticated
  using (
    bucket_id = 'documents'
    and public.is_vault_member(public.storage_object_vault_id(name))
  );

create policy "Vault members can upload document storage objects"
  on storage.objects for insert
  to authenticated
  with check (
    bucket_id = 'documents'
    and public.is_vault_member(public.storage_object_vault_id(name))
  );

comment on function public.storage_object_vault_id(text) is 'Extracts the vault id from document storage paths: vaults/{vault_id}/...';
