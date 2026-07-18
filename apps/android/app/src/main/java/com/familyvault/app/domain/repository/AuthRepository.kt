package com.familyvault.app.domain.repository

import com.familyvault.app.core.session.AppSessionState
import com.familyvault.app.domain.model.AccountInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeSessionState(): Flow<AppSessionState>
    fun observeAccountInfo(): Flow<AccountInfo?>
    suspend fun signInWithGoogle()
    suspend fun sendEmailSignInLink(email: String)
    suspend fun signOut()
}
