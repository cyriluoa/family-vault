package com.familyvault.app.feature.importfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.familyvault.app.core.ui.EmptyState
import com.familyvault.app.core.ui.FamilyVaultScaffold
import com.familyvault.app.core.ui.FamilyVaultScreenHeader

@Composable
fun ImportFileScreen(modifier: Modifier = Modifier) {
    FamilyVaultScaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EmptyState(
                title = "File ready to save",
                message = "Choose a subject, document type, and important dates before uploading."
            )
        }
    }
}
