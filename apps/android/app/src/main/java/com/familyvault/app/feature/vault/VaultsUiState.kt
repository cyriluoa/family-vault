package com.familyvault.app.feature.vault

import com.familyvault.app.domain.model.VaultSummary

data class VaultsUiState(
    val isLoading: Boolean = true,
    val vaults: List<VaultSummary> = emptyList(),
    val errorMessage: String? = null
) {
    val defaultVault: VaultSummary?
        get() = vaults.firstOrNull { it.isDefault } ?: vaults.firstOrNull()

    val otherVaults: List<VaultSummary>
        get() {
            val defaultVaultId = defaultVault?.id
            return vaults.filter { it.id != defaultVaultId }
        }
}
