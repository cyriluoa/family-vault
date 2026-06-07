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
- invite_status.
- invited_by.
- joined_at.
- created_at.

## users / profiles

Supabase Auth stores users. A profile table can store app-specific user details.

Likely fields:

- user_id.
- display_name.
- email.
- linked_person_subject_id.
- created_at.
- updated_at.

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
- created_at.
- updated_at.

Every vault should include one default family subject. A saved document should always point to a primary subject, and family-wide documents should use the Family subject.

## documents

Logical document records. A document can have many versions, one current version, one primary subject, and related subjects.

A saved document must have a primary subject. Upload drafts or import flows may temporarily be missing a primary subject until metadata is completed.

Categories and document types should start with a fixed MVP taxonomy. The data model should leave room for custom document types later without requiring custom type management in the first version.

Likely fields:

- id.
- vault_id.
- category.
- type.
- status.
- title.
- primary_subject_id.
- current_version_id.
- expires_at.
- created_by.
- created_at.
- updated_at.

Status values:

- active.
- inactive.

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

## document_metadata

Flexible key/value metadata for documents.

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
