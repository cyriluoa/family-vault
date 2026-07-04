package com.familyvault.app.core.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyvault.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<AppSessionState>(AppSessionState.Checking)
    val sessionState: StateFlow<AppSessionState> = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeSessionState().collect { sessionState ->
                _sessionState.value = sessionState
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                authRepository.signOut()
            }.onFailure {
                _sessionState.value = AppSessionState.SignedOut
            }
        }
    }
}