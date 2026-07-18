package com.familyvault.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.feature.auth.AuthScreen
import com.familyvault.app.feature.auth.AuthViewModel
import com.familyvault.app.feature.documents.DocumentsScreen
import com.familyvault.app.feature.importfile.ImportFileScreen
import com.familyvault.app.feature.onboarding.OnboardingScreen
import com.familyvault.app.feature.profile.ProfileRoute
import com.familyvault.app.feature.vault.CreateVaultRoute
import com.familyvault.app.feature.vault.VaultsRoute
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    startDestination: AppRoute = AppRoute.Auth,
    navController: NavHostController = rememberNavController(),
    accountInfo: AccountInfo? = null
) {

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(AppRoute.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authUiState = authViewModel.uiState.collectAsStateWithLifecycle()

            AuthScreen(
                isLoading = authUiState.value.isLoading,
                errorMessage = authUiState.value.errorMessage,
                infoMessage = authUiState.value.infoMessage,
                emailAuthStep = authUiState.value.emailAuthStep,
                email = authUiState.value.email,
                onGoogleSignInClick = authViewModel::onGoogleSignInClick,
                onEmailSignInClick = authViewModel::onEmailSignInClick,
                onEmailChanged = authViewModel::onEmailChanged,
                onSendEmailLinkClick = authViewModel::onSendEmailLinkClick,
                onBackToAuthOptionsClick = authViewModel::onBackToAuthOptionsClick
            )
        }

        composable(AppRoute.Onboarding.route) {
            OnboardingScreen(
                onCreateVaultClick = {
                    navController.navigate(AppRoute.CreateVault.route)
                }
            )
        }

        composable(AppRoute.CreateVault.route) {
            CreateVaultRoute(
                initialPersonName = accountInfo.defaultPersonName(),
                onBackClick = {
                    navController.popBackStack()
                },
                onCreated = {
                    navController.navigate(AppRoute.Vaults.route) {
                        popUpTo(AppRoute.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.Vaults.route) {
            VaultsRoute(
                onVaultClick = {
                    navController.navigate(AppRoute.Documents.route)
                },
                onCreateVaultClick = {
                    navController.navigate(AppRoute.CreateVault.route)
                },
                onJoinVaultClick = {
                    // Later: navigate to join-vault flow.
                }
            )
        }

        composable(AppRoute.Documents.route) {
            DocumentsScreen()
        }

        composable(AppRoute.ImportFile.route) {
            ImportFileScreen()
        }
        composable(AppRoute.Profile.route) {
            ProfileRoute()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavGraphPreview() {
    FamilyVaultTheme {
        AppNavGraph()
    }
}
private fun AccountInfo?.defaultPersonName(): String {
    val displayName = this?.displayName?.trim().orEmpty()
    if (displayName.isNotBlank()) return displayName

    val emailPrefix = this?.email
        ?.substringBefore('@')
        ?.replace('.', ' ')
        ?.replace('_', ' ')
        ?.trim()
        .orEmpty()

    return emailPrefix
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }
}
