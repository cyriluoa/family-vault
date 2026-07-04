package com.familyvault.app.domain.model

data class UserProfile(
    val id: String,
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val phone: String?,
    val providers: List<String>,
    val createdAt: String?,
    val lastSignInAt: String?,
    val emailConfirmedAt: String?
)