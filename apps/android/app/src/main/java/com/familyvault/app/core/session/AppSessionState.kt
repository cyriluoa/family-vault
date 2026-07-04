package com.familyvault.app.core.session

sealed interface AppSessionState {
    data object Checking : AppSessionState
    data object SignedOut : AppSessionState
    data object SignedIn : AppSessionState
}
