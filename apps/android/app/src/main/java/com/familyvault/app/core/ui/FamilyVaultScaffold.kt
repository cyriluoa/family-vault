package com.familyvault.app.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FamilyVaultScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        content(
            PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 24.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
        )
    }
}
