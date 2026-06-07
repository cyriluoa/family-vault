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
- A user may also have a linked person subject.
- A document has a category, type, status, primary subject, related subjects, tags, metadata, and versions.
- A saved document must have a primary subject. During an upload draft or import flow, the primary subject can be temporarily missing until metadata is completed.
- Categories and document types should use a fixed starter taxonomy for the MVP. The model should allow custom document types later, but custom type management is not part of the first version.
- Document status should be simple: active or inactive.
- Versioning should be simple: each document has one current version and older versions.
- The system should support latest-version tracking.
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

## Android Share-Sheet Flows

Users should be able to share a PDF, image, or document from apps such as WhatsApp, Gmail, Files, Google Drive, Adobe Reader, and similar apps into FamilyVault. The app should let the user select a vault or space, attach metadata, and upload the file directly into cloud storage managed by FamilyVault.

Users should also be able to share a document from FamilyVault to external apps. FamilyVault may temporarily download or cache the file locally when necessary, then invoke Android's native share sheet. The shared document should be sent as a file attachment, not as a public link.

Public document links are not required for the MVP and should be treated as a future feature.
