package com.familyvault.app.data.vault

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVaultRpcParams(
    @SerialName("vault_name")
    val vaultName: String,
    @SerialName("creator_person_name")
    val creatorPersonName: String
)
