package com.familyvault.app.feature.vault

sealed interface CreateVaultEvent {
    data class Created(val vaultId: String) : CreateVaultEvent
}
