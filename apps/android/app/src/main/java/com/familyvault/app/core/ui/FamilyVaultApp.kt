package com.familyvault.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.familyvault.app.core.navigation.AppNavGraph
import com.familyvault.app.core.navigation.AppRoute
import com.familyvault.app.core.session.AppSessionState
import com.familyvault.app.core.session.AppViewModel
import com.familyvault.app.core.session.SignedInStartDestination
import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun FamilyVaultApp(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val sessionState = appViewModel.sessionState.collectAsStateWithLifecycle()
    val accountInfo = appViewModel.accountInfo.collectAsStateWithLifecycle()
    val signedInStartDestination = appViewModel.signedInStartDestination.collectAsStateWithLifecycle()

    when (sessionState.value) {
        AppSessionState.Checking -> FamilyVaultLoadingScreen()
        AppSessionState.SignedOut -> AppNavGraph(startDestination = AppRoute.Auth)
        AppSessionState.SignedIn -> {
            val startDestination = signedInStartDestination.value
            if (startDestination == null) {
                FamilyVaultLoadingScreen()
            } else {
                SignedInAppShell(
                    accountInfo = accountInfo.value,
                    startDestination = startDestination.toAppRoute(),
                    onSignOutClick = appViewModel::signOut
                )
            }
        }
    }
}

@Composable
private fun SignedInAppShell(
    accountInfo: AccountInfo?,
    startDestination: AppRoute,
    onSignOutClick: () -> Unit
) {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavGraph(
            startDestination = startDestination,
            navController = navController,
            accountInfo = accountInfo
        )
        SignedInTopActions(
            accountInfo = accountInfo,
            onProfileClick = {
                navController.navigate(AppRoute.Profile.route) {
                    launchSingleTop = true
                }
            },
            onSignOutClick = onSignOutClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 4.dp, end = 8.dp)
        )
    }
}

@Composable
private fun SignedInTopActions(
    accountInfo: AccountInfo?,
    onProfileClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatarButton(
            accountInfo = accountInfo,
            onClick = onProfileClick
        )
        SignedInOverflowMenu(onSignOutClick = onSignOutClick)
    }
}

@Composable
private fun ProfileAvatarButton(
    accountInfo: AccountInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarUrl = accountInfo?.avatarUrl
    val initials = accountInitials(accountInfo)

    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SignedInOverflowMenu(
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            ThreeDotOverflowIcon()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Sign out") },
                onClick = {
                    expanded = false
                    onSignOutClick()
                }
            )
        }
    }
}

@Composable
private fun ThreeDotOverflowIcon() {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun FamilyVaultLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

private fun SignedInStartDestination.toAppRoute(): AppRoute {
    return when (this) {
        SignedInStartDestination.Onboarding -> AppRoute.Onboarding
        SignedInStartDestination.Vaults -> AppRoute.Vaults
    }
}

private fun accountInitials(accountInfo: AccountInfo?): String {
    val displayName = accountInfo?.displayName?.trim().orEmpty()
    if (displayName.isNotBlank()) {
        return displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    val email = accountInfo?.email.orEmpty()
    return email.firstOrNull()?.uppercase() ?: "FV"
}

@Preview(showBackground = true)
@Composable
private fun SignedInTopActionsPreview() {
    FamilyVaultTheme {
        SignedInTopActions(
            accountInfo = AccountInfo(
                userId = "preview",
                email = "cyril@example.com",
                phone = null,
                providers = listOf("google"),
                displayName = "Cyril Joseph",
                avatarUrl = null,
                lastSignInAt = null,
                emailConfirmedAt = null,
                createdAt = null
            ),
            onProfileClick = {},
            onSignOutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyVaultLoadingScreenPreview() {
    FamilyVaultTheme {
        FamilyVaultLoadingScreen()
    }
}
