# Database

This document describes the initial conceptual entities. It is not a finalized schema.

## vaults / spaces

Shared family groups. A vault is the main permission boundary for documents, subjects, members, and activity.

The schema should support multiple vaults per user from day one, even though the MVP Android UI is optimized around one default vault.

Likely fields:

- id.
- name.
- created_by.
- created_at.
- updated_at.

When a vault is created, it should also get a default Family subject for family-wide documents.

## vault_members

Connects users to vaults and records membership state and role.

MVP invites should use shareable invite links or codes. Email-based invites can be added later.

Likely fields:

- vault_id.
- user_id.
- role.
- status.
- person_subject_id.
- joined_at.
- created_at.
- updated_at.

`person_subject_id` is set only when an actual app user is linked to a person subject in that vault. Do not create `vault_members` rows for every person subject.

MVP roles:

- owner.
- member.

MVP statuses:

- active.
- removed.

## vault_invites

Shareable invite links or codes that can be sent through WhatsApp.

Store only a hash of the invite code, not the raw code.

Likely fields:

- id.
- vault_id.
- code_hash.
- created_by.
- role.
- expires_at.
- max_uses.
- use_count.
- revoked_at.
- created_at.
- updated_at.

## users / profiles

Supabase Auth stores users. A profile table can store app-specific user details.

Likely fields:

- id.
- display_name.
- email.
- avatar_url.
- default_vault_id.
- created_at.
- updated_at.

`default_vault_id` is only a UI preference for which vault opens first. Users can belong to many vaults through `vault_members`.

Do not store the linked person subject on `profiles`. The link between an app user and a person subject belongs on `vault_members.person_subject_id`, because that relationship is vault-specific.

## subjects

Things that documents are about. Subjects are separate from users.

Subject kinds:

- person.
- property.
- vehicle.
- trip.
- organization.
- family.

Likely fields:

- id.
- vault_id.
- kind.
- display_name.
- metadata.
- created_by.
- created_at.
- updated_at.

Every vault should include one default family subject. A saved document should always point to a primary subject, and family-wide documents should use the Family subject.

For the MVP, subject metadata should be filled by controlled app forms based on subject kind, not by exposing arbitrary key/value editing to users.

## document_types

Built-in and vault-specific document types.

System document types have no `vault_id` and are available to every vault. Custom document types belong to one vault.

Categories are fixed product-level buckets. Families can add custom document types, but custom types should still choose from the fixed category set.

Likely fields:

- id.
- vault_id.
- key.
- label.
- category.
- description.
- is_system.
- is_active.
- created_by.
- created_at.
- updated_at.

Examples:

- passport.
- tax_return.
- bank_statement.
- insurance_policy.
- vehicle_registration.
- flight_ticket.
- lab_result.
- other.

Fixed categories:

- identity.
- financial.
- insurance.
- property.
- vehicle.
- medical.
- education.
- travel.
- general.

## documents

Logical document records. A document can have many versions, one current version, one primary subject, and related subjects.

A saved document must have a primary subject. Upload drafts or import flows may temporarily be missing a primary subject until metadata is completed.

Categories and document types should start with built-in system document types. Vaults can later add their own custom document types while inheriting the system set.

Likely fields:

- id.
- vault_id.
- document_type_id.
- status.
- title.
- primary_subject_id.
- current_version_id.
- document_date.
- issued_at.
- expires_at.
- period_start.
- period_end.
- metadata.
- created_by.
- created_at.
- updated_at.

Status values:

- active.
- inactive.

For the MVP, do not add a dedicated issuing organization field. If needed, an issuing organization can be represented later as a subject linked to a document.

Current document fields such as `issued_at` and `expires_at` describe the latest/current real-world version that the family cares about. For example, a newly issued passport can be added as a new version of "Cyril's Passport," and the document row should update to the latest issue and expiry dates.

Use date fields according to the document lifecycle:

- `issued_at` and `expires_at` for renewable/current documents such as passports, licenses, visas, and insurance policies.
- `document_date` for event documents such as lab results, service records, receipts, and tickets.
- `period_start` and `period_end` for recurring period documents such as tax returns, bank statements, utility bills, pay slips, and report cards.

## document_subjects

Join table for related subjects on a document.

Likely fields:

- document_id.
- subject_id.
- relationship_type.
- created_at.

## document_versions

Stores file version records. Each version points to an object in cloud storage.

Likely fields:

- id.
- document_id.
- version_number.
- storage_bucket.
- storage_path.
- original_filename.
- content_type.
- file_size_bytes.
- checksum.
- uploaded_by.
- uploaded_at.

The current version is tracked from documents.current_version_id. Older versions remain available for history.

MVP version selection is manual. The app can ask whether a new upload is a new version of an existing document, but automatic detection is a future feature. User-facing copy should use current version and older versions.

`checksum` is nullable in the MVP. It can later store a file fingerprint, such as a SHA-256 hash, to help detect exact duplicate uploads and verify file integrity.

## document_metadata

Do not create this as a separate MVP table. Use `documents.metadata` for controlled document-type fields populated by the app.

Possible future table:

Likely fields:

- document_id.
- key.
- value.
- value_type.
- created_at.
- updated_at.

This can support fields such as expiry date, policy number, issuing organization, document date, and custom tags.

Expiry reminders are attached to documents first. Assigned members or responsible people can be added later.

## activity_logs

Audit trail for important vault and document actions.

Likely fields:

- id.
- vault_id.
- actor_user_id.
- action.
- target_type.
- target_id.
- metadata.
- created_at.

MVP actions:

- vault.created.
- member.joined.
- member.removed.
- invite.created.
- invite.revoked.
- invite.accepted.
- subject.created.
- subject.updated.
- subject.linked_to_member.
- document.created.
- document.updated.
- document.marked_active.
- document.marked_inactive.
- document.shared_external.
- document.opened_external.
- document_version.added.
- document_version.set_current.
- reminder.created.
- reminder.dismissed.

MVP target types:

- vault.
- vault_member.
- vault_invite.
- subject.
- document_type.
- document.
- document_version.
- reminder.

## processing_jobs

Tracks asynchronous processing for OCR, classification, embeddings, reminders, and other future worker tasks.

This is a placeholder concept for future OCR and AI processing. Do not implement OCR or AI behavior in the MVP schema beyond leaving room for processing jobs.

Likely fields:

- id.
- vault_id.
- document_id.
- document_version_id.
- job_type.
- status.
- attempts.
- error_message.
- created_at.
- updated_at.
- completed_at.
