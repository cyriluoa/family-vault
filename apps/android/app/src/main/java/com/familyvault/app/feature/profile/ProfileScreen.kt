package com.familyvault.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.familyvault.app.core.ui.FamilyVaultScaffold
import com.familyvault.app.core.ui.FamilyVaultScreenHeader
import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.domain.model.AppProfile
import com.familyvault.app.ui.theme.FamilyVaultTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState.value,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier
) {
    FamilyVaultScaffold(
        modifier = modifier,
        header = { FamilyVaultScreenHeader(title = "Profile") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {

            ProfileHeader(
                appProfile = uiState.appProfile,
                accountInfo = uiState.accountInfo
            )

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ProfileSection(title = "FamilyVault Profile") {
                val profile = uiState.appProfile
                ProfileField(label = "Display name", value = profile?.displayName.orEmptyValue())
                ProfileField(label = "Email", value = profile?.email.orEmptyValue())
                ProfileField(label = "Created", value = profile?.createdAt.formatTimestamp())
                ProfileField(label = "Updated", value = profile?.updatedAt.formatTimestamp(), showDivider = false)
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProfileSection(title = "Account") {
                val account = uiState.accountInfo
                ProfileField(label = "Providers", value = account?.providers.orEmpty().joinToString().ifBlank { "Not set" })
                ProfileField(label = "Account email", value = account?.email.orEmptyValue())
                ProfileField(label = "Phone", value = account?.phone.orEmptyValue())
                ProfileField(label = "Account created", value = account?.createdAt.formatTimestamp())
                ProfileField(label = "Last sign in", value = account?.lastSignInAt.formatTimestamp())
                ProfileField(label = "Email confirmed", value = account?.emailConfirmedAt.formatTimestamp(), showDivider = false)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    appProfile: AppProfile?,
    accountInfo: AccountInfo?
) {
    val displayName = appProfile?.displayName
        ?: accountInfo?.email?.substringBefore('@')
        ?: "FamilyVault user"
    val email = appProfile?.email ?: accountInfo?.email ?: "No email available"
    val avatarUrl = appProfile?.avatarUrl

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AvatarImage(
                avatarUrl = avatarUrl,
                initials = initialsFor(displayName)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AvatarImage(
    avatarUrl: String?,
    initials: String
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
            )
        }
    }
}

private fun String?.orEmptyValue(): String = this?.takeIf { it.isNotBlank() } ?: "Not set"

private fun String?.formatTimestamp(): String {
    val rawValue = this?.takeIf { it.isNotBlank() } ?: return "Not set"
    return try {
        val instant = Instant.parse(rawValue)
        ProfileDateFormatter.format(instant)
    } catch (_: DateTimeParseException) {
        rawValue
    }
}

private val ProfileDateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("MMM d, yyyy, h:mm a")
    .withZone(ZoneId.systemDefault())

private fun initialsFor(value: String): String {
    return value
        .split(' ', '.', '_', '-')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercaseChar().toString() }
        .ifBlank { "FV" }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FamilyVaultTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                isLoading = false,
                appProfile = AppProfile(
                    id = "profile-id",
                    email = "cyril@example.com",
                    displayName = "Cyril",
                    avatarUrl = "https://example.com/avatar.png",
                    defaultVaultId = "default-vault-id",
                    createdAt = "2026-06-24T00:00:00Z",
                    updatedAt = "2026-06-24T00:10:00Z"
                ),
                accountInfo = AccountInfo(
                    userId = "auth-user-id",
                    email = "cyril@example.com",
                    phone = null,
                    providers = listOf("google"),
                    displayName = "Cyril Joseph",
                    avatarUrl = null,
                    createdAt = "2026-06-24T00:00:00Z",
                    lastSignInAt = "2026-06-24T00:10:00Z",
                    emailConfirmedAt = "2026-06-24T00:00:00Z"
                )
            )
        )
    }
}
