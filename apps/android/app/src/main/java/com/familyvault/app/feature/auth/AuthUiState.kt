package com.familyvault.app.feature.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
