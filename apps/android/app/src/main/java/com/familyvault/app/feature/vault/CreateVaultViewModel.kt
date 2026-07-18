package com.familyvault.app.feature.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyvault.app.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateVaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVaultUiState())
    val uiState: StateFlow<CreateVaultUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<CreateVaultEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun applyInitialPersonName(personName: String) {
        if (personName.isBlank()) return

        _uiState.update { currentState ->
            if (currentState.personName.isBlank()) {
                currentState.copy(personName = personName)
            } else {
                currentState
            }
        }
    }

    fun onVaultNameChanged(vaultName: String) {
        _uiState.update {
            it.copy(vaultName = vaultName, errorMessage = null)
        }
    }

    fun onPersonNameChanged(personName: String) {
        _uiState.update {
            it.copy(personName = personName, errorMessage = null)
        }
    }

    fun onCreateVaultClick() {
        val currentState = uiState.value
        val vaultName = currentState.vaultName.trim()
        val personName = currentState.personName.trim()

        if (vaultName.isBlank() || personName.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter a vault name and your name in this vault.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            runCatching {
                vaultRepository.createVault(
                    vaultName = vaultName,
                    creatorPersonName = personName
                )
            }.onSuccess { vaultId ->
                _uiState.update { it.copy(isLoading = false) }
                eventChannel.send(CreateVaultEvent.Created(vaultId))
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not create vault."
                    )
                }
            }
        }
    }
}
