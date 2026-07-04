package com.familyvault.app.feature.documents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.familyvault.app.core.ui.EmptyState
import com.familyvault.app.core.ui.FamilyVaultScaffold
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun DocumentsScreen(modifier: Modifier = Modifier) {
    FamilyVaultScaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EmptyState(
                title = "No documents yet",
                message = "Shared files and saved family records will appear here."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentsScreenPreview() {
    FamilyVaultTheme {
        DocumentsScreen()
    }
}
