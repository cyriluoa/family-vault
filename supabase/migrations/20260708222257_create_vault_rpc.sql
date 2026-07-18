-- FamilyVault create-vault RPC.
-- Creates the first usable vault structure for the authenticated user in one database transaction.

create or replace function public.create_vault(
  vault_name text,
  creator_person_name text
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  current_user_id uuid := auth.uid();
  cleaned_vault_name text := nullif(btrim(vault_name), '');
  cleaned_person_name text := nullif(btrim(creator_person_name), '');
  new_vault_id uuid;
  family_subject_id uuid;
  person_subject_id uuid;
begin
  if current_user_id is null then
    raise exception 'create_vault requires an authenticated user';
  end if;

  if cleaned_vault_name is null then
    raise exception 'vault_name is required';
  end if;

  if cleaned_person_name is null then
    raise exception 'creator_person_name is required';
  end if;

  if not exists (
    select 1
    from public.profiles p
    where p.id = current_user_id
  ) then
    raise exception 'profile must exist before creating a vault';
  end if;

  insert into public.vaults (name, created_by)
  values (cleaned_vault_name, current_user_id)
  returning id into new_vault_id;

  insert into public.subjects (vault_id, kind, display_name, created_by)
  values (new_vault_id, 'family', 'Family', current_user_id)
  returning id into family_subject_id;

  insert into public.subjects (vault_id, kind, display_name, created_by)
  values (new_vault_id, 'person', cleaned_person_name, current_user_id)
  returning id into person_subject_id;

  insert into public.vault_members (
    vault_id,
    user_id,
    role,
    status,
    person_subject_id,
    joined_at
  )
  values (
    new_vault_id,
    current_user_id,
    'owner',
    'active',
    person_subject_id,
    now()
  );

  update public.profiles
  set default_vault_id = new_vault_id
  where id = current_user_id
    and default_vault_id is null;

  insert into public.activity_logs (
    vault_id,
    actor_user_id,
    action,
    target_type,
    target_id,
    metadata
  )
  values (
    new_vault_id,
    current_user_id,
    'vault.created',
    'vault',
    new_vault_id,
    jsonb_build_object(
      'family_subject_id', family_subject_id,
      'creator_person_subject_id', person_subject_id
    )
  );

  return new_vault_id;
end;
$$;

revoke all on function public.create_vault(text, text) from public;
grant execute on function public.create_vault(text, text) to authenticated;

comment on function public.create_vault(text, text) is 'Creates a vault, default Family subject, creator person subject, owner membership, default vault preference, and activity log for the authenticated user.';
