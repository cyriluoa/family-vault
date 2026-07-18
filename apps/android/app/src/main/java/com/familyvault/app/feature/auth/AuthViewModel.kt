package com.familyvault.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyvault.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                authRepository.signInWithGoogle()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not start Google sign in."
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEmailSignInClick() {
        _uiState.update {
            it.copy(
                emailAuthStep = EmailAuthStep.EnterEmail,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(email = email, errorMessage = null)
        }
    }

    fun onBackToAuthOptionsClick() {
        _uiState.update {
            it.copy(
                emailAuthStep = EmailAuthStep.Hidden,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun onSendEmailLinkClick() {
        viewModelScope.launch {
            val email = uiState.value.email.trim()

            if (email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Enter your email address.") }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    infoMessage = null
                )
            }

            runCatching {
                authRepository.sendEmailSignInLink(email)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        email = email,
                        emailAuthStep = EmailAuthStep.LinkSent,
                        infoMessage = "We emailed a secure sign-in link to $email."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not send sign-in email."
                    )
                }
            }
        }
    }
}
