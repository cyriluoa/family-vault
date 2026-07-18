package com.familyvault.app.domain.repository

import com.familyvault.app.domain.model.VaultSummary

interface VaultRepository {
    suspend fun createVault(
        vaultName: String,
        creatorPersonName: String
    ): String

    suspend fun getMyVaults(): List<VaultSummary>
}
