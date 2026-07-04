# Design System

FamilyVault should feel calm, private, organized, trustworthy, and fast to scan. The product should feel like a secure family filing cabinet, not a social app, marketing site, or heavy enterprise tool.

## Cross-Platform Principles

- Keep screens quiet and utilitarian.
- Prioritize scanning, comparison, and repeated use.
- Use restrained color and avoid decorative gradients, blobs, and novelty visuals.
- Make file actions explicit and predictable.
- Do not surprise users with sharing, downloading, caching, or destructive actions.
- Use consistent spacing, typography, and empty/loading/error states across Android and web.

## Color Tokens

These tokens should be mirrored across Android Compose and the future web app.

| Token | Hex | Use |
| --- | --- | --- |
| `primary` | `#166B5B` | Primary actions, selected states, trusted brand color |
| `primary_dark` | `#0E4F43` | Pressed/dark primary states |
| `primary_soft` | `#E3F1ED` | Subtle selected backgrounds |
| `background` | `#F7F8F6` | App background |
| `surface` | `#FFFFFF` | Cards, sheets, dialogs, elevated surfaces |
| `surface_alt` | `#EEF3F0` | Secondary surfaces and quiet bands |
| `text_primary` | `#1B1F1E` | Main text |
| `text_secondary` | `#66706C` | Metadata and supporting text |
| `border` | `#DDE3DF` | Dividers and outlines |
| `warning` | `#B7791F` | Expiry and attention states |
| `warning_soft` | `#FFF4D8` | Subtle warning backgrounds |
| `error` | `#B42318` | Destructive/error states |
| `error_soft` | `#FDE7E4` | Subtle error backgrounds |

## Typography

Use platform defaults first, but keep hierarchy restrained:

- Screen titles should be confident, not hero-sized.
- Document titles should be easy to scan in lists.
- Metadata labels should be compact and muted.
- Buttons should be clear and direct.

Avoid oversized in-app headings. The app is a tool, not a landing page.

## Shape And Spacing

- Prefer 8dp corner radius for cards, chips, and framed UI.
- Use 16dp screen padding on mobile.
- Use 12dp spacing between related controls.
- Use subtle dividers or low-contrast surfaces instead of heavy card nesting.

## Core Components

Android and web should share these component concepts:

- App scaffold with safe-area-aware screen padding.
- Empty state.
- Loading state.
- Error state.
- Primary and secondary buttons.
- Document list item.
- Metadata chip.
- Status chip.
- Expiry/warning chip.

Avoid a persistent top app bar by default. Use inline screen headers, bottom navigation, contextual actions, or focused task headers only where they help the workflow.

## Document List

Document rows should show:

- Title.
- Primary subject.
- Document type/category.
- Important date, period, or expiry.
- Status only when useful.
- Small related subject/tag chips when they clarify the result.

The list should be dense enough to scan, but not cramped.

## Import Flow

The import flow is a focused task:

1. File received.
2. Choose vault.
3. Choose subject.
4. Choose document type.
5. Add relevant dates/details.
6. Save.

Show only fields that matter for the selected type/lifecycle. Avoid generic key/value forms in the MVP.

## Profile Screen

The profile screen should separate app profile data from auth account data.

- FamilyVault Profile: display name, email, avatar, created/updated dates, and later editable user preferences.
- Account: provider information, email confirmation, phone, account creation, and last sign-in.
- Do not show internal ids in the normal UI unless a debug/support mode is added.
- Show avatars visually when available, with initials as the fallback.
- Format timestamps in a readable local format instead of raw ISO strings.
- Profile and account details must be scrollable on small screens.