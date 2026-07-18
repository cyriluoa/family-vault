package com.familyvault.app.core.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.domain.repository.AuthRepository
import com.familyvault.app.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<AppSessionState>(AppSessionState.Checking)
    val sessionState: StateFlow<AppSessionState> = _sessionState.asStateFlow()

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo.asStateFlow()

    private val _signedInStartDestination = MutableStateFlow<SignedInStartDestination?>(null)
    val signedInStartDestination: StateFlow<SignedInStartDestination?> =
        _signedInStartDestination.asStateFlow()

    private var signedInDestinationJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.observeSessionState().collect { sessionState ->
                _sessionState.value = sessionState
                when (sessionState) {
                    AppSessionState.Checking -> _signedInStartDestination.value = null
                    AppSessionState.SignedOut -> clearSignedInState()
                    AppSessionState.SignedIn -> loadSignedInStartDestination()
                }
            }
        }

        viewModelScope.launch {
            authRepository.observeAccountInfo().collect { accountInfo ->
                _accountInfo.value = accountInfo
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                authRepository.signOut()
            }.onFailure {
                clearSignedInState()
                _sessionState.value = AppSessionState.SignedOut
            }
        }
    }

    private fun loadSignedInStartDestination() {
        signedInDestinationJob?.cancel()
        signedInDestinationJob = viewModelScope.launch {
            _signedInStartDestination.value = null

            val destination = runCatching {
                vaultRepository.getMyVaults()
            }.fold(
                onSuccess = { vaults ->
                    if (vaults.isEmpty()) {
                        SignedInStartDestination.Onboarding
                    } else {
                        SignedInStartDestination.Vaults
                    }
                },
                onFailure = {
                    SignedInStartDestination.Onboarding
                }
            )

            _signedInStartDestination.value = destination
        }
    }

    private fun clearSignedInState() {
        signedInDestinationJob?.cancel()
        signedInDestinationJob = null
        _signedInStartDestination.value = null
        _accountInfo.value = null
    }
}