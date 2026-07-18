package com.familyvault.app.feature.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.familyvault.app.core.ui.FamilyVaultScaffold
import com.familyvault.app.core.ui.GoogleSignInButton
import com.familyvault.app.ui.theme.FamilyVaultTheme

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    infoMessage: String? = null,
    emailAuthStep: EmailAuthStep = EmailAuthStep.Hidden,
    email: String = "",
    onGoogleSignInClick: () -> Unit = {},
    onEmailSignInClick: () -> Unit = {},
    onEmailChanged: (String) -> Unit = {},
    onSendEmailLinkClick: () -> Unit = {},
    onBackToAuthOptionsClick: () -> Unit = {}
) {
    BackHandler(enabled = emailAuthStep != EmailAuthStep.Hidden) {
        onBackToAuthOptionsClick()
    }

    FamilyVaultScaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            AuthHero(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isLoading = isLoading,
                errorMessage = errorMessage,
                infoMessage = infoMessage,
                emailAuthStep = emailAuthStep,
                email = email,
                onGoogleSignInClick = onGoogleSignInClick,
                onEmailSignInClick = onEmailSignInClick,
                onEmailChanged = onEmailChanged,
                onSendEmailLinkClick = onSendEmailLinkClick,
                onBackToAuthOptionsClick = onBackToAuthOptionsClick
            )

            Text(
                text = "Your documents are protected by account access and vault permissions.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthHero(
    isLoading: Boolean,
    errorMessage: String?,
    infoMessage: String?,
    emailAuthStep: EmailAuthStep,
    email: String,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onSendEmailLinkClick: () -> Unit,
    onBackToAuthOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FV",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "FamilyVault",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Passports, policies, records - everything your family needs, always within reach.",
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (emailAuthStep) {
                EmailAuthStep.Hidden -> AuthOptions(
                    isLoading = isLoading,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onEmailSignInClick = onEmailSignInClick
                )

                EmailAuthStep.EnterEmail -> EmailEntryForm(
                    email = email,
                    isLoading = isLoading,
                    onEmailChanged = onEmailChanged,
                    onSendEmailLinkClick = onSendEmailLinkClick,
                    onBackToAuthOptionsClick = onBackToAuthOptionsClick
                )

                EmailAuthStep.LinkSent -> EmailLinkSent(
                    email = email,
                    isLoading = isLoading,
                    onResendEmailClick = onSendEmailLinkClick,
                    onUseAnotherEmailClick = onEmailSignInClick
                )
            }
        }

        if (infoMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = infoMessage,
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthOptions(
    isLoading: Boolean,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit
) {
    GoogleSignInButton(
        onClick = onGoogleSignInClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        isLoading = isLoading,
        showRecommendedBadge = true
    )

    OutlinedButton(
        onClick = onEmailSignInClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = "Continue with email",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun EmailEntryForm(
    email: String,
    isLoading: Boolean,
    onEmailChanged: (String) -> Unit,
    onSendEmailLinkClick: () -> Unit,
    onBackToAuthOptionsClick: () -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChanged,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        singleLine = true,
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )

    Button(
        onClick = onSendEmailLinkClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = if (isLoading) "Sending..." else "Email me a sign-in link")
    }

    OutlinedButton(
        onClick = onBackToAuthOptionsClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(text = "Back")
    }
}

@Composable
private fun EmailLinkSent(
    email: String,
    isLoading: Boolean,
    onResendEmailClick: () -> Unit,
    onUseAnotherEmailClick: () -> Unit
) {
    Text(
        text = "Check your email",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )

    Text(
        text = "Open the secure sign-in link we emailed to $email on this device.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Button(
        onClick = onResendEmailClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = if (isLoading) "Sending..." else "Resend email")
    }

    OutlinedButton(
        onClick = onUseAnotherEmailClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(text = "Use another email")
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    FamilyVaultTheme {
        AuthScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenEmailPreview() {
    FamilyVaultTheme {
        AuthScreen(emailAuthStep = EmailAuthStep.EnterEmail, email = "cyril@example.com")
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenLinkSentPreview() {
    FamilyVaultTheme {
        AuthScreen(
            emailAuthStep = EmailAuthStep.LinkSent,
            email = "cyril@example.com",
            infoMessage = "We emailed a secure sign-in link to cyril@example.com."
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenLoadingPreview() {
    FamilyVaultTheme {
        AuthScreen(isLoading = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenErrorPreview() {
    FamilyVaultTheme {
        AuthScreen(errorMessage = "Could not sign in. Check your connection and try again.")
    }
}
