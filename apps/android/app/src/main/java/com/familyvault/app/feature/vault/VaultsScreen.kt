package com.familyvault.app.feature.vault

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familyvault.app.core.ui.FamilyVaultScaffold
import com.familyvault.app.core.ui.FamilyVaultScreenHeader
import com.familyvault.app.domain.model.VaultSummary
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun VaultsRoute(
    onVaultClick: (VaultSummary) -> Unit,
    onCreateVaultClick: () -> Unit,
    onJoinVaultClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VaultsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    VaultsScreen(
        uiState = uiState.value,
        onVaultClick = onVaultClick,
        onCreateVaultClick = onCreateVaultClick,
        onJoinVaultClick = onJoinVaultClick,
        onRetryClick = viewModel::loadVaults,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultsScreen(
    uiState: VaultsUiState,
    onVaultClick: (VaultSummary) -> Unit,
    onCreateVaultClick: () -> Unit,
    onJoinVaultClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showVaultActions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    FamilyVaultScaffold(
        modifier = modifier,
        header = {
            FamilyVaultScreenHeader(
                title = "Your vaults",
                subtitle = "Choose a family space to continue."
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.errorMessage != null -> VaultsErrorState(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick
                    )
                    uiState.vaults.isEmpty() -> VaultsEmptyState(
                        onCreateVaultClick = onCreateVaultClick,
                        onJoinVaultClick = onJoinVaultClick
                    )
                    else -> VaultsContent(
                        uiState = uiState,
                        onVaultClick = onVaultClick
                    )
                }
            }

            if (!uiState.isLoading && uiState.errorMessage == null && uiState.vaults.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showVaultActions = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 24.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(18.dp),
                    text = {
                        Text(
                            text = "Add",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    icon = {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        }
    }

    if (showVaultActions) {
        ModalBottomSheet(
            onDismissRequest = { showVaultActions = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            VaultActionsSheet(
                onCreateVaultClick = {
                    showVaultActions = false
                    onCreateVaultClick()
                },
                onJoinVaultClick = {
                    showVaultActions = false
                    onJoinVaultClick()
                }
            )
        }
    }
}

@Composable
private fun VaultsContent(
    uiState: VaultsUiState,
    onVaultClick: (VaultSummary) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        uiState.defaultVault?.let { defaultVault ->
            SectionLabel(text = "Default")
            VaultRow(
                vault = defaultVault,
                emphasized = true,
                onClick = { onVaultClick(defaultVault) }
            )
        }

        if (uiState.otherVaults.isNotEmpty()) {
            SectionLabel(text = "Other vaults")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.otherVaults.forEach { vault ->
                    VaultRow(
                        vault = vault,
                        emphasized = false,
                        onClick = { onVaultClick(vault) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun VaultRow(
    vault: VaultSummary,
    emphasized: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (emphasized) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (emphasized) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = if (emphasized) 0.dp else 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (emphasized) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (emphasized) {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = vaultInitial(vault.name),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = vault.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = roleLabel(vault.role),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.72f)
                )
            }

            if (vault.isDefault) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Default",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun VaultActionsSheet(
    onCreateVaultClick: () -> Unit,
    onJoinVaultClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Add a vault",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Create a new family space or join one with an invite code.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onCreateVaultClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Create vault",
                fontWeight = FontWeight.SemiBold
            )
        }
        OutlinedButton(
            onClick = onJoinVaultClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(text = "Join with invite code")
        }
    }
}

@Composable
private fun VaultsEmptyState(
    onCreateVaultClick: () -> Unit,
    onJoinVaultClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "No vaults yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Create your first family vault or join one someone has invited you to.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onCreateVaultClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Create your first vault")
        }
        OutlinedButton(
            onClick = onJoinVaultClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("Join a vault")
        }
    }
}

@Composable
private fun VaultsErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Could not load vaults",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Try again")
        }
    }
}

private fun roleLabel(role: String): String {
    return when (role) {
        "owner" -> "Owner"
        "member" -> "Member"
        else -> role.replaceFirstChar { it.uppercase() }
    }
}

private fun vaultInitial(name: String): String {
    return name.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "V"
}

@Preview(showBackground = true)
@Composable
private fun VaultsScreenPreview() {
    FamilyVaultTheme {
        VaultsScreen(
            uiState = VaultsUiState(
                isLoading = false,
                vaults = listOf(
                    VaultSummary(
                        id = "1",
                        name = "Joseph Family",
                        role = "owner",
                        isDefault = true,
                        joinedAt = null
                    ),
                    VaultSummary(
                        id = "2",
                        name = "Parents Documents",
                        role = "member",
                        isDefault = false,
                        joinedAt = null
                    )
                )
            ),
            onVaultClick = {},
            onCreateVaultClick = {},
            onJoinVaultClick = {},
            onRetryClick = {}
        )
    }
}
