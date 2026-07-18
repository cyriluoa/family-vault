-- FamilyVault vault-list RPC.
-- Returns the active vaults available to the authenticated user in a client-friendly shape.

create or replace function public.my_vaults()
returns table (
  vault_id uuid,
  vault_name text,
  role text,
  is_default boolean,
  joined_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
  select
    v.id as vault_id,
    v.name as vault_name,
    vm.role,
    (p.default_vault_id = v.id) as is_default,
    vm.joined_at
  from public.vault_members vm
  join public.vaults v
    on v.id = vm.vault_id
  join public.profiles p
    on p.id = vm.user_id
  where vm.user_id = auth.uid()
    and vm.status = 'active'
  order by
    (p.default_vault_id = v.id) desc,
    case vm.role when 'owner' then 0 else 1 end,
    coalesce(vm.joined_at, vm.created_at) desc,
    v.name asc;
$$;

revoke all on function public.my_vaults() from public;
grant execute on function public.my_vaults() to authenticated;

comment on function public.my_vaults() is 'Returns active vault memberships for the authenticated user, with the default vault first.';
