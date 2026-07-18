package com.familyvault.app.domain.model

data class AccountInfo(
    val userId: String,
    val email: String?,
    val phone: String?,
    val providers: List<String>,
    val displayName: String?,
    val avatarUrl: String?,
    val lastSignInAt: String?,
    val emailConfirmedAt: String?,
    val createdAt: String?
)
