-- FamilyVault initial core schema.
-- Covers app profiles, vaults, membership, subjects, invite codes, and core RLS.

create extension if not exists pgcrypto;

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null,
  email text,
  avatar_url text,
  default_vault_id uuid,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint profiles_display_name_not_blank check (length(trim(display_name)) > 0)
);

create table public.vaults (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  created_by uuid not null references public.profiles(id) on delete restrict,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint vaults_name_not_blank check (length(trim(name)) > 0)
);

alter table public.profiles
  add constraint profiles_default_vault_id_fkey
  foreign key (default_vault_id) references public.vaults(id) on delete set null;

create table public.subjects (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  kind text not null,
  display_name text not null,
  metadata jsonb not null default '{}'::jsonb,
  created_by uuid references public.profiles(id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint subjects_kind_check check (
    kind in ('family', 'person', 'property', 'vehicle', 'trip', 'organization')
  ),
  constraint subjects_display_name_not_blank check (length(trim(display_name)) > 0),
  constraint subjects_metadata_object_check check (jsonb_typeof(metadata) = 'object'),
  constraint subjects_vault_id_id_unique unique (vault_id, id)
);

create unique index subjects_one_family_per_vault_idx
  on public.subjects (vault_id)
  where kind = 'family';

create table public.vault_members (
  vault_id uuid not null references public.vaults(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  role text not null default 'member',
  status text not null default 'active',
  person_subject_id uuid references public.subjects(id) on delete set null,
  joined_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  primary key (vault_id, user_id),
  constraint vault_members_role_check check (role in ('owner', 'member')),
  constraint vault_members_status_check check (status in ('active', 'removed'))
);

create unique index vault_members_one_active_user_per_person_subject_idx
  on public.vault_members (vault_id, person_subject_id)
  where person_subject_id is not null and status = 'active';

create table public.vault_invites (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  code_hash text not null unique,
  created_by uuid not null references public.profiles(id) on delete restrict,
  role text not null default 'member',
  expires_at timestamptz,
  max_uses integer,
  use_count integer not null default 0,
  revoked_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint vault_invites_role_check check (role in ('member')),
  constraint vault_invites_max_uses_check check (max_uses is null or max_uses > 0),
  constraint vault_invites_use_count_check check (use_count >= 0)
);

create index vault_members_user_id_idx on public.vault_members (user_id);
create index vault_members_vault_id_status_idx on public.vault_members (vault_id, status);
create index subjects_vault_id_kind_idx on public.subjects (vault_id, kind);
create index vault_invites_vault_id_idx on public.vault_invites (vault_id);

create trigger profiles_set_updated_at
  before update on public.profiles
  for each row execute function public.set_updated_at();

create trigger vaults_set_updated_at
  before update on public.vaults
  for each row execute function public.set_updated_at();

create trigger subjects_set_updated_at
  before update on public.subjects
  for each row execute function public.set_updated_at();

create trigger vault_members_set_updated_at
  before update on public.vault_members
  for each row execute function public.set_updated_at();

create trigger vault_invites_set_updated_at
  before update on public.vault_invites
  for each row execute function public.set_updated_at();

create or replace function public.ensure_default_vault_membership()
returns trigger
language plpgsql
as $$
begin
  if new.default_vault_id is null then
    return new;
  end if;

  if not exists (
    select 1
    from public.vault_members vm
    where vm.vault_id = new.default_vault_id
      and vm.user_id = new.id
      and vm.status = 'active'
  ) then
    raise exception 'default_vault_id must reference a vault where the user is an active member';
  end if;

  return new;
end;
$$;

create trigger profiles_ensure_default_vault_membership
  before insert or update on public.profiles
  for each row execute function public.ensure_default_vault_membership();

create or replace function public.ensure_person_subject_link()
returns trigger
language plpgsql
as $$
begin
  if new.person_subject_id is null then
    return new;
  end if;

  if not exists (
    select 1
    from public.subjects s
    where s.id = new.person_subject_id
      and s.vault_id = new.vault_id
      and s.kind = 'person'
  ) then
    raise exception 'person_subject_id must reference a person subject in the same vault';
  end if;

  return new;
end;
$$;

create trigger vault_members_ensure_person_subject_link
  before insert or update of vault_id, person_subject_id on public.vault_members
  for each row execute function public.ensure_person_subject_link();

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (id, display_name, email, avatar_url)
  values (
    new.id,
    coalesce(
      new.raw_user_meta_data->>'display_name',
      new.raw_user_meta_data->>'name',
      split_part(new.email, '@', 1),
      'FamilyVault user'
    ),
    new.email,
    new.raw_user_meta_data->>'avatar_url'
  )
  on conflict (id) do nothing;

  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

create or replace function public.is_vault_member(target_vault_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.vault_members vm
    where vm.vault_id = target_vault_id
      and vm.user_id = auth.uid()
      and vm.status = 'active'
  );
$$;

create or replace function public.has_vault_role(target_vault_id uuid, allowed_roles text[])
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.vault_members vm
    where vm.vault_id = target_vault_id
      and vm.user_id = auth.uid()
      and vm.status = 'active'
      and vm.role = any(allowed_roles)
  );
$$;

create or replace function public.shares_active_vault_with(target_user_id uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.vault_members self_vm
    join public.vault_members other_vm
      on other_vm.vault_id = self_vm.vault_id
    where self_vm.user_id = auth.uid()
      and self_vm.status = 'active'
      and other_vm.user_id = target_user_id
      and other_vm.status = 'active'
  );
$$;

alter table public.profiles enable row level security;
alter table public.vaults enable row level security;
alter table public.subjects enable row level security;
alter table public.vault_members enable row level security;
alter table public.vault_invites enable row level security;

grant usage on schema public to authenticated;
grant select, insert, update on public.profiles to authenticated;
grant select, insert, update on public.vaults to authenticated;
grant select, insert, update on public.subjects to authenticated;
grant select, insert, update, delete on public.vault_members to authenticated;
grant select, insert, update, delete on public.vault_invites to authenticated;

create policy "Users can read their own and co-member profiles"
  on public.profiles for select
  to authenticated
  using (id = auth.uid() or public.shares_active_vault_with(id));

create policy "Users can update their own profile"
  on public.profiles for update
  to authenticated
  using (id = auth.uid())
  with check (id = auth.uid());

create policy "Users can insert their own profile"
  on public.profiles for insert
  to authenticated
  with check (id = auth.uid());

create policy "Members can read their vaults"
  on public.vaults for select
  to authenticated
  using (public.is_vault_member(id));

create policy "Authenticated users can create vaults they own"
  on public.vaults for insert
  to authenticated
  with check (created_by = auth.uid());

create policy "Owners can update their vaults"
  on public.vaults for update
  to authenticated
  using (public.has_vault_role(id, array['owner']))
  with check (public.has_vault_role(id, array['owner']));

create policy "Members can read vault membership"
  on public.vault_members for select
  to authenticated
  using (public.is_vault_member(vault_id));

create policy "Owners can manage vault membership"
  on public.vault_members for all
  to authenticated
  using (public.has_vault_role(vault_id, array['owner']))
  with check (public.has_vault_role(vault_id, array['owner']));

create policy "Vault creators can add their initial owner membership"
  on public.vault_members for insert
  to authenticated
  with check (
    user_id = auth.uid()
    and role = 'owner'
    and status = 'active'
    and exists (
      select 1
      from public.vaults v
      where v.id = vault_id
        and v.created_by = auth.uid()
    )
  );

create policy "Members can read subjects"
  on public.subjects for select
  to authenticated
  using (public.is_vault_member(vault_id));

create policy "Members can create subjects"
  on public.subjects for insert
  to authenticated
  with check (public.is_vault_member(vault_id));

create policy "Members can update subjects"
  on public.subjects for update
  to authenticated
  using (public.is_vault_member(vault_id))
  with check (public.is_vault_member(vault_id));

create policy "Owners can read vault invites"
  on public.vault_invites for select
  to authenticated
  using (public.has_vault_role(vault_id, array['owner']));

create policy "Owners can create vault invites"
  on public.vault_invites for insert
  to authenticated
  with check (
    created_by = auth.uid()
    and public.has_vault_role(vault_id, array['owner'])
  );

create policy "Owners can update vault invites"
  on public.vault_invites for update
  to authenticated
  using (public.has_vault_role(vault_id, array['owner']))
  with check (public.has_vault_role(vault_id, array['owner']));

create policy "Owners can delete vault invites"
  on public.vault_invites for delete
  to authenticated
  using (public.has_vault_role(vault_id, array['owner']));

comment on table public.profiles is 'FamilyVault app profile for a Supabase Auth user. Vault-specific identity lives in vault_members.';
comment on column public.profiles.default_vault_id is 'UI preference for the vault to open first; users can belong to many vaults.';
comment on table public.vaults is 'Shared family spaces. Main permission boundary for subjects, documents, and activity.';
comment on table public.vault_members is 'Per-vault membership and role. person_subject_id links an app user to their person subject in this vault.';
comment on table public.subjects is 'People, properties, vehicles, trips, organizations, and the default Family subject that documents are about.';
comment on column public.subjects.metadata is 'Controlled flexible metadata populated by typed app forms, not free-form user key/value editing in the MVP.';
comment on table public.vault_invites is 'Shareable invite links/codes. Store only code hashes, never raw invite codes.';
