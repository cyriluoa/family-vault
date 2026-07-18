package com.familyvault.app.domain.model

data class VaultSummary(
    val id: String,
    val name: String,
    val role: String,
    val isDefault: Boolean,
    val joinedAt: String?
)
