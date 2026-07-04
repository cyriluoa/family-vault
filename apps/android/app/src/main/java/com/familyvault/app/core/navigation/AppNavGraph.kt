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
import com.familyvault.app.feature.auth.AuthScreen
import com.familyvault.app.feature.auth.AuthViewModel
import com.familyvault.app.feature.documents.DocumentsScreen
import com.familyvault.app.feature.importfile.ImportFileScreen
import com.familyvault.app.feature.onboarding.OnboardingScreen
import com.familyvault.app.feature.profile.ProfileRoute
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    startDestination: AppRoute = AppRoute.Auth,
    navController: NavHostController = rememberNavController()
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
                onGoogleSignInClick = authViewModel::onGoogleSignInClick,
                onEmailSignInClick = authViewModel::onEmailSignInClick
            )
        }

        composable(AppRoute.Onboarding.route) {
            OnboardingScreen()
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
