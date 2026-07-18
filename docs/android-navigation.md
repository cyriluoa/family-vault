# Android Navigation

FamilyVault uses Jetpack Compose navigation instead of XML navigation graphs.

The central graph lives in:

```text
apps/android/app/src/main/java/com/familyvault/app/core/navigation/AppNavGraph.kt
```

Routes are modeled in `AppRoute.kt` as a sealed interface. This keeps route names centralized and lets future routes carry arguments, such as `DocumentDetail(documentId)`.

## Current Routes

```text
Auth
Onboarding
CreateVault
Vaults
Documents
ImportFile
Profile
```

## Current Routing Behavior

```mermaid
flowchart TD
  Start["App launch"] --> Session{"Supabase session?"}
  Session -->|Checking| Loading["Loading"]
  Session -->|No session| Auth["Auth"]
  Session -->|Signed in| VaultLookup{"Active vaults?"}
  VaultLookup -->|No| OnboardingShell["Signed-in shell: Onboarding"]
  VaultLookup -->|Yes| VaultsShell["Signed-in shell: Vaults"]
  OnboardingShell --> Onboarding["Onboarding"]
  VaultsShell --> Vaults["Vaults"]
  OnboardingShell --> Profile["Profile"]
  VaultsShell --> Profile
  OnboardingShell --> SignOut["Sign out"]
  VaultsShell --> SignOut
  SignOut --> Auth
  Onboarding --> CreateVault["Create Vault"]
  CreateVault -->|RPC success| Vaults
  Vaults --> Documents["Documents"]
  Vaults --> CreateVault
  Documents --> ImportFile["Import File"]
  ImportFile --> Documents
```

`FamilyVaultApp` owns app-level session routing. `AppViewModel` resolves the signed-in start destination by calling the `public.my_vaults` RPC. `SignedInAppShell` owns the signed-in `NavHostController`; the profile avatar navigates to Profile and the overflow menu handles secondary actions such as sign out.

If the user has no active vault memberships, the signed-in shell starts at Onboarding. If active memberships exist, it starts at Vaults. Create Vault can create the first vault through the `public.create_vault` RPC and then routes to Vaults. Vaults loads the current user's active vault memberships through the `public.my_vaults` RPC. The populated list uses a bottom-right add action that opens Create Vault or Join with invite code options.