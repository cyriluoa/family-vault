package com.familyvault.app.feature.profile

import com.familyvault.app.domain.model.AccountInfo
import com.familyvault.app.domain.model.AppProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val appProfile: AppProfile? = null,
    val accountInfo: AccountInfo? = null,
    val errorMessage: String? = null
)