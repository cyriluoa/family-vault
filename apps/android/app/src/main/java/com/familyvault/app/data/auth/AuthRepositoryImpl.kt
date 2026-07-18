package com.familyvault.app.data.auth

import com.familyvault.app.core.session.AppSessionState
import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override fun observeSessionState(): Flow<AppSessionState> {
        return supabaseClient.auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> AppSessionState.SignedIn
                is SessionStatus.NotAuthenticated -> AppSessionState.SignedOut
                is SessionStatus.RefreshFailure -> AppSessionState.SignedOut
                SessionStatus.Initializing -> AppSessionState.Checking
            }
        }
    }

    override fun observeAccountInfo(): Flow<AccountInfo?> {
        return supabaseClient.auth.sessionStatus.map { sessionStatus ->
            (sessionStatus as? SessionStatus.Authenticated)?.session?.user?.toAccountInfo()
        }
    }

    override suspend fun signInWithGoogle() {
        supabaseClient.auth.signInWith(Google)
    }

    override suspend fun sendEmailSignInLink(email: String) {
        supabaseClient.auth.signInWith(OTP) {
            this.email = email
        }
    }

    override suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    private fun UserInfo.toAccountInfo(): AccountInfo {
        val displayName = metadataValue("display_name")
            ?: metadataValue("full_name")
            ?: metadataValue("name")
        val avatarUrl = metadataValue("avatar_url")
            ?: metadataValue("picture")

        return AccountInfo(
            userId = id,
            email = email,
            phone = phone,
            providers = identities.orEmpty().map { it.provider }.distinct(),
            displayName = displayName,
            avatarUrl = avatarUrl,
            lastSignInAt = lastSignInAt?.toString(),
            emailConfirmedAt = emailConfirmedAt?.toString(),
            createdAt = createdAt?.toString()
        )
    }

    private fun UserInfo.metadataValue(key: String): String? {
        return (userMetadata?.get(key) as? JsonPrimitive)?.contentOrNull
    }
}
