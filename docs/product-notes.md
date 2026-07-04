# Product Notes

FamilyVault is a private family document management app. The goal is to replace messy WhatsApp document sharing with a secure vault where family members can upload, organize, search, version, and manage important documents.

## Core Model

- A space or vault is a shared family group.
- The data model should support multiple vaults from day one, but the MVP Android UI should optimize around one default vault.
- Members of a vault can access documents in that vault by default.
- Restricted or private document access should be supported later, but the MVP should stay simple.
- Invites should start as invite links or codes that can be shared through WhatsApp. Email-based invites can come later.
- Documents should not be organized primarily by folders. FamilyVault should use metadata-first organization.
- Documents can be associated with subjects.
- Subjects can be people, properties, vehicles, trips, organizations, or family-wide entities.
- Every vault should start with a default Family subject. Family-wide documents should attach to that subject.
- Users are app accounts. Subjects are what documents are about.
- Google sign-in and email sign-in should be treated as ways to access the same FamilyVault account when they use the same verified email address.
- User-facing auth copy should say Continue with Google and Continue with email, not imply that these always create separate accounts.
- A user's membership in a vault may link to a person subject in that vault.
- A document has a category, type, status, primary subject, related subjects, tags, metadata, and versions.
- A saved document must have a primary subject. During an upload draft or import flow, the primary subject can be temporarily missing until metadata is completed.
- Categories and document types should start with built-in system document types. Vaults should be able to add custom document types later while inheriting the built-in set.
- Document status should be simple: active or inactive.
- Versioning should be simple: each document has one current version and older versions.
- The system should support latest-version tracking.
- A renewed real-world document, such as a newly issued passport, can be treated as a new version of the same logical document when the family thinks of it as the same ongoing record.
- Current document fields such as issued date and expiry date should describe the latest/current version that the family cares about.
- Different document lifecycles need different date fields: issue/expiry dates for renewable documents, document date for event documents, and period start/end for recurring documents.
- New-version detection is manual in the MVP. The app may ask whether an uploaded file is a new version of an existing document, but no automatic detection is required initially.
- Expiry reminders are attached to documents first. Assigned members or responsible people can be added later.
- Files are stored in cloud storage, not permanently on-device.
- Viewing is external in the MVP: open files in another Android app instead of building an internal PDF viewer.
- Keep user-facing version language simple: current version and older versions. Avoid exposing internal terms like superseded.

## MVP Must-Haves

- Family spaces/vaults.
- Member invites.
- Invite links/codes that can be shared through WhatsApp.
- Android share-sheet upload.
- Upload PDFs/images/docs to cloud storage.
- Person/subject-centric organization.
- Document categories/types.
- Active/inactive document status.
- Current/older document versions.
- Latest-version tracking.
- Search/filtering.
- Expiry reminders.
- Activity log.
- Open files externally.
- Strong backend auth and permissions.
- Google sign-in.
- Email sign-in via OTP or magic link.
- Same-profile behavior when the same verified email is used across Google and email sign-in.

## Nice-to-Haves

- OCR.
- Automatic document classification.
- Metadata extraction such as person/type/expiry.
- Semantic/AI search.
- Natural-language search.
- Missing-document detection.
- Emergency/travel mode.
- Document summaries.
- Action-required workflows.
- Timeline view.
- Web app access.
- Email-based invites.
- Custom document type management.
- Issuing organization as a dedicated document field.
- Smart file optimization suggestions for large scans/photos when the optimized copy remains usable and saves meaningful storage.

## Android Share-Sheet Flows

Users should be able to share a PDF, image, or document from apps such as WhatsApp, Gmail, Files, Google Drive, Adobe Reader, and similar apps into FamilyVault. The app should let the user select a vault or space, attach metadata, and upload the file directly into cloud storage managed by FamilyVault.

Users should also be able to share a document from FamilyVault to external apps. FamilyVault may temporarily download or cache the file locally when necessary, then invoke Android's native share sheet. The shared document should be sent as a file attachment, not as a public link.

Public document links are not required for the MVP and should be treated as a future feature.

## Storage Optimization

Original uploaded files should remain the source of truth. Do not silently apply destructive compression to important documents.

A future smart optimization flow can detect when a file is likely worth reducing, such as oversized scanned PDFs or phone photos of paper. If an optimized copy would remain readable and save meaningful space, for example reducing storage to roughly a third of the original, FamilyVault should encourage the user to use the optimized version or keep both.

The app should not encourage optimization for files that are already compressed, files where quality would noticeably suffer, or files where fidelity is legally important unless the user explicitly chooses it.
