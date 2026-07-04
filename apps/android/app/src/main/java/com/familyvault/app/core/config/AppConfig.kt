package com.familyvault.app.core.config

import com.familyvault.app.BuildConfig

object AppConfig {
    const val AppName = "FamilyVault"
    val SupabaseUrl: String = BuildConfig.SUPABASE_URL
    val SupabasePublishableKey: String = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    val AuthRedirectUri: String = BuildConfig.AUTH_REDIRECT_URI
}
