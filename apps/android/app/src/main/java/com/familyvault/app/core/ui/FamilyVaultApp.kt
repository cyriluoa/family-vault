package com.familyvault.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.familyvault.app.core.navigation.AppNavGraph
import com.familyvault.app.core.navigation.AppRoute
import com.familyvault.app.core.session.AppSessionState
import com.familyvault.app.core.session.AppViewModel
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun FamilyVaultApp(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val sessionState = appViewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState.value) {
        AppSessionState.Checking -> FamilyVaultLoadingScreen()
        AppSessionState.SignedOut -> AppNavGraph(startDestination = AppRoute.Auth)
        AppSessionState.SignedIn -> SignedInAppShell(
            onSignOutClick = appViewModel::signOut
        )
    }
}

@Composable
private fun SignedInAppShell(
    onSignOutClick: () -> Unit
) {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavGraph(
            startDestination = AppRoute.Onboarding,
            navController = navController
        )
        SignedInOverflowMenu(
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
private fun SignedInOverflowMenu(
    onProfileClick: () -> Unit,
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
                text = { Text(text = "Profile") },
                onClick = {
                    expanded = false
                    onProfileClick()
                }
            )
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

@Preview(showBackground = true)
@Composable
private fun SignedInOverflowMenuPreview() {
    FamilyVaultTheme {
        SignedInOverflowMenu(onProfileClick = {}, onSignOutClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyVaultLoadingScreenPreview() {
    FamilyVaultTheme {
        FamilyVaultLoadingScreen()
    }
}