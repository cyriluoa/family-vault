package com.familyvault.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyvault.app.domain.repository.AuthRepository
import com.familyvault.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeAccountInfo()
        loadAppProfile()
    }

    private fun observeAccountInfo() {
        viewModelScope.launch {
            authRepository.observeAccountInfo().collect { accountInfo ->
                _uiState.update { it.copy(accountInfo = accountInfo) }
            }
        }
    }

    private fun loadAppProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                profileRepository.getCurrentProfile()
            }.onSuccess { appProfile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        appProfile = appProfile,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load profile."
                    )
                }
            }
        }
    }
}