package com.familyvault.app.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.familyvault.app.core.ui.EmptyState
import com.familyvault.app.core.ui.FamilyVaultScaffold

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    FamilyVaultScaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EmptyState(
                title = "Create or join a family vault",
                message = "Start with one default vault, then invite family members."
            )
        }
    }
}
