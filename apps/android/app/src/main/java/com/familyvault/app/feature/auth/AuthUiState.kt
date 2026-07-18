package com.familyvault.app.feature.auth

enum class EmailAuthStep {
    Hidden,
    EnterEmail,
    LinkSent
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val emailAuthStep: EmailAuthStep = EmailAuthStep.Hidden,
    val email: String = ""
)
