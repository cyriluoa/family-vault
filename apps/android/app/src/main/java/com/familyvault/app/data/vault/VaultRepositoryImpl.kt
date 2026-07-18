package com.familyvault.app.data.vault

import com.familyvault.app.domain.model.VaultSummary
import com.familyvault.app.domain.repository.VaultRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import javax.inject.Inject

class VaultRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : VaultRepository {

    override suspend fun createVault(
        vaultName: String,
        creatorPersonName: String
    ): String {
        return supabaseClient.postgrest.rpc(
            function = "create_vault",
            parameters = CreateVaultRpcParams(
                vaultName = vaultName,
                creatorPersonName = creatorPersonName
            )
        ).decodeAs()
    }

    override suspend fun getMyVaults(): List<VaultSummary> {
        return supabaseClient.postgrest
            .rpc("my_vaults")
            .decodeList<VaultSummaryRow>()
            .map { it.toVaultSummary() }
    }
}
