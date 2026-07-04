package com.familyvault.app.domain.model

data class AppProfile(
    val id: String,
    val displayName: String?,
    val email: String?,
    val avatarUrl: String?,
    val defaultVaultId: String?,
    val createdAt: String?,
    val updatedAt: String?
)