package com.familyvault.app.feature.vault

data class CreateVaultUiState(
    val vaultName: String = "",
    val personName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = vaultName.trim().isNotBlank() && personName.trim().isNotBlank()
}
