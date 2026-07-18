package com.familyvault.app.data.vault

import com.familyvault.app.domain.model.VaultSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VaultSummaryRow(
    @SerialName("vault_id")
    val id: String,
    @SerialName("vault_name")
    val name: String,
    val role: String,
    @SerialName("is_default")
    val isDefault: Boolean,
    @SerialName("joined_at")
    val joinedAt: String? = null
) {
    fun toVaultSummary(): VaultSummary {
        return VaultSummary(
            id = id,
            name = name,
            role = role,
            isDefault = isDefault,
            joinedAt = joinedAt
        )
    }
}
