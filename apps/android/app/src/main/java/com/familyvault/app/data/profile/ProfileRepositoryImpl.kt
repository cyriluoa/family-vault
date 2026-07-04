package com.familyvault.app.data.profile

import com.familyvault.app.domain.model.AppProfile
import com.familyvault.app.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ProfileRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ProfileRepository {

    override suspend fun getCurrentProfile(): AppProfile? {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        return supabaseClient.postgrest["profiles"]
            .select {
                filter {
                    eq("id", userId)
                }
                limit(1)
            }
            .decodeSingleOrNull<ProfileRow>()
            ?.toAppProfile()
    }

    private fun ProfileRow.toAppProfile(): AppProfile {
        return AppProfile(
            id = id,
            displayName = displayName,
            email = email,
            avatarUrl = avatarUrl,
            defaultVaultId = defaultVaultId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

@Serializable
private data class ProfileRow(
    val id: String,
    @SerialName("display_name")
    val displayName: String? = null,
    val email: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("default_vault_id")
    val defaultVaultId: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)