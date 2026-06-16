-- FamilyVault document schema.
-- Documents are logical family records; document_versions are uploaded files.

create table public.document_types (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid references public.vaults(id) on delete cascade,
  key text not null,
  label text not null,
  category text not null,
  description text,
  is_system boolean not null default false,
  is_active boolean not null default true,
  created_by uuid references public.profiles(id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint document_types_key_not_blank check (length(trim(key)) > 0),
  constraint document_types_label_not_blank check (length(trim(label)) > 0),
  constraint document_types_category_not_blank check (length(trim(category)) > 0),
  constraint document_types_category_check check (
    category in (
      'identity',
      'financial',
      'insurance',
      'property',
      'vehicle',
      'medical',
      'education',
      'travel',
      'general'
    )
  ),
  constraint document_types_system_scope_check check (
    (is_system = true and vault_id is null)
    or (is_system = false and vault_id is not null)
  )
);

create unique index document_types_system_key_unique_idx
  on public.document_types (key)
  where vault_id is null;

create unique index document_types_vault_key_unique_idx
  on public.document_types (vault_id, key)
  where vault_id is not null;

create table public.documents (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  title text not null,
  document_type_id uuid not null references public.document_types(id) on delete restrict,
  status text not null default 'active',
  primary_subject_id uuid not null references public.subjects(id) on delete restrict,
  current_version_id uuid,
  document_date date,
  issued_at date,
  expires_at date,
  period_start date,
  period_end date,
  metadata jsonb not null default '{}'::jsonb,
  created_by uuid not null references public.profiles(id) on delete restrict,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint documents_title_not_blank check (length(trim(title)) > 0),
  constraint documents_status_check check (status in ('active', 'inactive')),
  constraint documents_metadata_object_check check (jsonb_typeof(metadata) = 'object'),
  constraint documents_period_check check (
    period_start is null or period_end is null or period_end >= period_start
  ),
  constraint documents_vault_id_id_unique unique (vault_id, id)
);

create table public.document_versions (
  id uuid primary key default gen_random_uuid(),
  document_id uuid not null references public.documents(id) on delete cascade,
  version_number integer not null,
  storage_bucket text not null default 'documents',
  storage_path text not null,
  original_filename text,
  content_type text,
  file_size_bytes bigint,
  checksum text,
  uploaded_by uuid not null references public.profiles(id) on delete restrict,
  uploaded_at timestamptz not null default now(),
  constraint document_versions_version_number_check check (version_number > 0),
  constraint document_versions_file_size_check check (file_size_bytes is null or file_size_bytes >= 0),
  constraint document_versions_document_version_unique unique (document_id, version_number),
  constraint document_versions_storage_object_unique unique (storage_bucket, storage_path)
);

alter table public.documents
  add constraint documents_current_version_id_fkey
  foreign key (current_version_id)
  references public.document_versions(id)
  on delete set null;

create table public.document_subjects (
  document_id uuid not null references public.documents(id) on delete cascade,
  subject_id uuid not null references public.subjects(id) on delete cascade,
  relationship_type text,
  created_at timestamptz not null default now(),
  primary key (document_id, subject_id)
);

create table public.document_tags (
  document_id uuid not null references public.documents(id) on delete cascade,
  tag text not null,
  created_at timestamptz not null default now(),
  primary key (document_id, tag),
  constraint document_tags_tag_not_blank check (length(trim(tag)) > 0)
);

create table public.reminders (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  document_id uuid not null references public.documents(id) on delete cascade,
  reminder_type text not null default 'expiry',
  remind_on date not null,
  status text not null default 'pending',
  created_by uuid not null references public.profiles(id) on delete restrict,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint reminders_type_check check (reminder_type in ('expiry')),
  constraint reminders_status_check check (status in ('pending', 'sent', 'dismissed'))
);

create table public.activity_logs (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  actor_user_id uuid references public.profiles(id) on delete set null,
  action text not null,
  target_type text not null,
  target_id uuid,
  metadata jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  constraint activity_logs_action_check check (
    action in (
      'vault.created',
      'member.joined',
      'member.removed',
      'invite.created',
      'invite.revoked',
      'invite.accepted',
      'subject.created',
      'subject.updated',
      'subject.linked_to_member',
      'document.created',
      'document.updated',
      'document.marked_active',
      'document.marked_inactive',
      'document.shared_external',
      'document.opened_external',
      'document_version.added',
      'document_version.set_current',
      'reminder.created',
      'reminder.dismissed'
    )
  ),
  constraint activity_logs_target_type_check check (
    target_type in (
      'vault',
      'vault_member',
      'vault_invite',
      'subject',
      'document_type',
      'document',
      'document_version',
      'reminder'
    )
  ),
  constraint activity_logs_metadata_object_check check (jsonb_typeof(metadata) = 'object')
);

create table public.processing_jobs (
  id uuid primary key default gen_random_uuid(),
  vault_id uuid not null references public.vaults(id) on delete cascade,
  document_id uuid references public.documents(id) on delete cascade,
  document_version_id uuid references public.document_versions(id) on delete cascade,
  job_type text not null,
  status text not null default 'queued',
  attempts integer not null default 0,
  error_message text,
  metadata jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  completed_at timestamptz,
  constraint processing_jobs_status_check check (status in ('queued', 'running', 'complete', 'failed')),
  constraint processing_jobs_attempts_check check (attempts >= 0),
  constraint processing_jobs_metadata_object_check check (jsonb_typeof(metadata) = 'object')
);

create index document_types_vault_id_category_idx on public.document_types (vault_id, category);
create index documents_vault_id_status_idx on public.documents (vault_id, status);
create index documents_vault_id_document_type_idx on public.documents (vault_id, document_type_id);
create index documents_vault_id_primary_subject_idx on public.documents (vault_id, primary_subject_id);
create index documents_expires_at_idx on public.documents (expires_at) where expires_at is not null;
create index documents_document_date_idx on public.documents (document_date) where document_date is not null;
create index documents_period_idx on public.documents (period_start, period_end) where period_start is not null or period_end is not null;
create index document_versions_document_id_idx on public.document_versions (document_id);
create index document_subjects_subject_id_idx on public.document_subjects (subject_id);
create index document_tags_tag_idx on public.document_tags (tag);
create index reminders_vault_id_status_remind_on_idx on public.reminders (vault_id, status, remind_on);
create index activity_logs_vault_id_created_at_idx on public.activity_logs (vault_id, created_at desc);
create index processing_jobs_status_created_at_idx on public.processing_jobs (status, created_at);

create trigger document_types_set_updated_at
  before update on public.document_types
  for each row execute function public.set_updated_at();

create trigger documents_set_updated_at
  before update on public.documents
  for each row execute function public.set_updated_at();

create trigger reminders_set_updated_at
  before update on public.reminders
  for each row execute function public.set_updated_at();

create trigger processing_jobs_set_updated_at
  before update on public.processing_jobs
  for each row execute function public.set_updated_at();

create or replace function public.document_vault_id(target_document_id uuid)
returns uuid
language sql
stable
security definer
set search_path = public
as $$
  select d.vault_id
  from public.documents d
  where d.id = target_document_id;
$$;

create or replace function public.ensure_document_subject_links_same_vault()
returns trigger
language plpgsql
as $$
declare
  document_vault uuid;
  subject_vault uuid;
begin
  select d.vault_id into document_vault
  from public.documents d
  where d.id = new.document_id;

  select s.vault_id into subject_vault
  from public.subjects s
  where s.id = new.subject_id;

  if document_vault is null or subject_vault is null or document_vault <> subject_vault then
    raise exception 'document subject links must stay within the same vault';
  end if;

  return new;
  
end;
$$;

create trigger document_subjects_same_vault
  before insert or update of document_id, subject_id on public.document_subjects
  for each row execute function public.ensure_document_subject_links_same_vault();

create or replace function public.ensure_document_type_allowed()
returns trigger
language plpgsql
as $$
begin
  if not exists (
    select 1
    from public.document_types dt
    where dt.id = new.document_type_id
      and dt.is_active = true
      and (dt.vault_id is null or dt.vault_id = new.vault_id)
  ) then
    raise exception 'document_type_id must reference an active system type or an active type from the same vault';
  end if;

  return new;
end;
$$;

create trigger documents_type_allowed
  before insert or update of vault_id, document_type_id on public.documents
  for each row execute function public.ensure_document_type_allowed();

create or replace function public.ensure_document_primary_subject_same_vault()
returns trigger
language plpgsql
as $$
begin
  if not exists (
    select 1
    from public.subjects s
    where s.id = new.primary_subject_id
      and s.vault_id = new.vault_id
  ) then
    raise exception 'primary_subject_id must reference a subject in the same vault';
  end if;

  return new;
end;
$$;

create trigger documents_primary_subject_same_vault
  before insert or update of vault_id, primary_subject_id on public.documents
  for each row execute function public.ensure_document_primary_subject_same_vault();

create or replace function public.ensure_current_version_belongs_to_document()
returns trigger
language plpgsql
as $$
begin
  if new.current_version_id is null then
    return new;
  end if;

  if not exists (
    select 1
    from public.document_versions dv
    where dv.id = new.current_version_id
      and dv.document_id = new.id
  ) then
    raise exception 'current_version_id must reference a version of the same document';
  end if;

  return new;
end;
$$;

create trigger documents_current_version_same_document
  before update of current_version_id on public.documents
  for each row execute function public.ensure_current_version_belongs_to_document();

create or replace function public.ensure_reminder_document_same_vault()
returns trigger
language plpgsql
as $$
begin
  if public.document_vault_id(new.document_id) <> new.vault_id then
    raise exception 'reminder document must belong to the same vault';
  end if;

  return new;
end;
$$;

create trigger reminders_document_same_vault
  before insert or update of vault_id, document_id on public.reminders
  for each row execute function public.ensure_reminder_document_same_vault();

create or replace function public.ensure_processing_job_same_vault()
returns trigger
language plpgsql
as $$
begin
  if new.document_id is not null and public.document_vault_id(new.document_id) <> new.vault_id then
    raise exception 'processing job document must belong to the same vault';
  end if;

  if new.document_version_id is not null and not exists (
    select 1
    from public.document_versions dv
    join public.documents d on d.id = dv.document_id
    where dv.id = new.document_version_id
      and d.vault_id = new.vault_id
  ) then
    raise exception 'processing job document version must belong to the same vault';
  end if;

  return new;
end;
$$;

create trigger processing_jobs_same_vault
  before insert or update of vault_id, document_id, document_version_id on public.processing_jobs
  for each row execute function public.ensure_processing_job_same_vault();

alter table public.document_types enable row level security;
alter table public.documents enable row level security;
alter table public.document_versions enable row level security;
alter table public.document_subjects enable row level security;
alter table public.document_tags enable row level security;
alter table public.reminders enable row level security;
alter table public.activity_logs enable row level security;
alter table public.processing_jobs enable row level security;

grant select, insert, update on public.document_types to authenticated;
grant select, insert, update on public.documents to authenticated;
grant select, insert on public.document_versions to authenticated;
grant select, insert, update, delete on public.document_subjects to authenticated;
grant select, insert, update, delete on public.document_tags to authenticated;
grant select, insert, update, delete on public.reminders to authenticated;
grant select, insert on public.activity_logs to authenticated;
grant select on public.processing_jobs to authenticated;

create policy "Authenticated users can read system document types"
  on public.document_types for select
  to authenticated
  using (vault_id is null and is_system = true and is_active = true);

create policy "Members can read vault document types"
  on public.document_types for select
  to authenticated
  using (vault_id is not null and public.is_vault_member(vault_id));

create policy "Owners can create vault document types"
  on public.document_types for insert
  to authenticated
  with check (
    vault_id is not null
    and is_system = false
    and created_by = auth.uid()
    and public.has_vault_role(vault_id, array['owner'])
  );

create policy "Owners can update vault document types"
  on public.document_types for update
  to authenticated
  using (vault_id is not null and is_system = false and public.has_vault_role(vault_id, array['owner']))
  with check (vault_id is not null and is_system = false and public.has_vault_role(vault_id, array['owner']));

create policy "Members can read documents"
  on public.documents for select
  to authenticated
  using (public.is_vault_member(vault_id));

create policy "Members can create documents"
  on public.documents for insert
  to authenticated
  with check (public.is_vault_member(vault_id) and created_by = auth.uid());

create policy "Members can update documents"
  on public.documents for update
  to authenticated
  using (public.is_vault_member(vault_id))
  with check (public.is_vault_member(vault_id));

create policy "Members can read document versions"
  on public.document_versions for select
  to authenticated
  using (public.is_vault_member(public.document_vault_id(document_id)));

create policy "Members can create document versions"
  on public.document_versions for insert
  to authenticated
  with check (public.is_vault_member(public.document_vault_id(document_id)) and uploaded_by = auth.uid());

create policy "Members can read document subjects"
  on public.document_subjects for select
  to authenticated
  using (public.is_vault_member(public.document_vault_id(document_id)));

create policy "Members can manage document subjects"
  on public.document_subjects for all
  to authenticated
  using (public.is_vault_member(public.document_vault_id(document_id)))
  with check (public.is_vault_member(public.document_vault_id(document_id)));

create policy "Members can read document tags"
  on public.document_tags for select
  to authenticated
  using (public.is_vault_member(public.document_vault_id(document_id)));

create policy "Members can manage document tags"
  on public.document_tags for all
  to authenticated
  using (public.is_vault_member(public.document_vault_id(document_id)))
  with check (public.is_vault_member(public.document_vault_id(document_id)));

create policy "Members can read reminders"
  on public.reminders for select
  to authenticated
  using (public.is_vault_member(vault_id));

create policy "Members can manage reminders"
  on public.reminders for all
  to authenticated
  using (public.is_vault_member(vault_id))
  with check (public.is_vault_member(vault_id));

create policy "Members can read activity logs"
  on public.activity_logs for select
  to authenticated
  using (public.is_vault_member(vault_id));

create policy "Members can create activity logs"
  on public.activity_logs for insert
  to authenticated
  with check (public.is_vault_member(vault_id) and actor_user_id = auth.uid());

create policy "Members can read processing jobs"
  on public.processing_jobs for select
  to authenticated
  using (public.is_vault_member(vault_id));

insert into public.document_types (key, label, category, description, is_system)
values
  ('passport', 'Passport', 'identity', 'Passport or travel identity document.', true),
  ('driver_license', 'Driver License', 'identity', 'Driver license or driving permit.', true),
  ('national_id', 'National ID', 'identity', 'National identity card or government ID.', true),
  ('birth_certificate', 'Birth Certificate', 'identity', 'Birth certificate or equivalent record.', true),
  ('marriage_certificate', 'Marriage Certificate', 'identity', 'Marriage certificate or equivalent record.', true),
  ('visa_or_permit', 'Visa or Permit', 'identity', 'Visa, residence permit, entry permit, or immigration document.', true),
  ('tax_return', 'Tax Return', 'financial', 'Annual tax return or filed tax package.', true),
  ('tax_notice', 'Tax Notice', 'financial', 'Tax assessment, notice, or tax authority letter.', true),
  ('bank_statement', 'Bank Statement', 'financial', 'Bank or account statement for a period.', true),
  ('pay_slip', 'Pay Slip', 'financial', 'Pay slip or payroll statement.', true),
  ('loan_or_mortgage', 'Loan or Mortgage', 'financial', 'Loan, mortgage, refinance, or credit agreement.', true),
  ('insurance_policy', 'Insurance Policy', 'insurance', 'Insurance policy, certificate, or renewal document.', true),
  ('insurance_card', 'Insurance Card', 'insurance', 'Insurance card or proof of coverage.', true),
  ('insurance_claim', 'Insurance Claim', 'insurance', 'Insurance claim, claim document, or claim correspondence.', true),
  ('property_deed', 'Property Deed', 'property', 'Property deed, title, or ownership document.', true),
  ('lease_agreement', 'Lease Agreement', 'property', 'Lease, rental agreement, or tenancy document.', true),
  ('utility_bill', 'Utility Bill', 'property', 'Utility bill or utility statement.', true),
  ('vehicle_registration', 'Vehicle Registration', 'vehicle', 'Vehicle registration or renewal document.', true),
  ('vehicle_title', 'Vehicle Title', 'vehicle', 'Vehicle title or ownership document.', true),
  ('vehicle_service_record', 'Vehicle Service Record', 'vehicle', 'Vehicle service invoice, inspection, or repair record.', true),
  ('medical_report', 'Medical Report', 'medical', 'Medical report, visit summary, or doctor letter.', true),
  ('lab_result', 'Lab Result', 'medical', 'Lab result or diagnostic report.', true),
  ('prescription', 'Prescription', 'medical', 'Prescription or medication document.', true),
  ('vaccination_record', 'Vaccination Record', 'medical', 'Vaccination or immunization record.', true),
  ('degree_or_diploma', 'Degree or Diploma', 'education', 'Degree, diploma, certificate, or credential.', true),
  ('transcript', 'Transcript', 'education', 'Academic transcript or marksheet.', true),
  ('report_card', 'Report Card', 'education', 'School report card or term report.', true),
  ('flight_ticket', 'Flight Ticket', 'travel', 'Flight ticket, boarding pass, or itinerary receipt.', true),
  ('hotel_booking', 'Hotel Booking', 'travel', 'Hotel or accommodation booking.', true),
  ('travel_itinerary', 'Travel Itinerary', 'travel', 'Trip itinerary or travel plan.', true),
  ('receipt', 'Receipt', 'general', 'Receipt or proof of payment.', true),
  ('contract', 'Contract', 'general', 'Contract, agreement, or signed document.', true),
  ('other', 'Other', 'general', 'General document type for items that do not fit another type.', true);

comment on table public.document_types is 'Built-in and vault-specific document types. System types have vault_id null; custom family types belong to one vault.';
comment on table public.documents is 'Logical family document records. Current/latest fields live here; files live in document_versions.';
comment on column public.documents.metadata is 'Controlled document-type fields from the app. Skip a separate document_metadata table for MVP.';
comment on table public.document_versions is 'Uploaded file versions. checksum is a nullable file fingerprint such as SHA-256.';
comment on table public.document_subjects is 'Related subjects beyond documents.primary_subject_id.';
comment on table public.document_tags is 'Simple document tags for filtering.';
comment on table public.reminders is 'Document reminders, starting with expiry reminders.';
comment on table public.activity_logs is 'Audit trail for vault and document activity.';
comment on table public.processing_jobs is 'Placeholder for future OCR, AI, embeddings, and other background processing.';
