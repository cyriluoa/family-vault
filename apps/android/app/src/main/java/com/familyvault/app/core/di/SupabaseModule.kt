package com.familyvault.app.core.di

import com.familyvault.app.core.config.AppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.ExternalAuthAction
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = AppConfig.SupabaseUrl,
            supabaseKey = AppConfig.SupabasePublishableKey
        ) {
            install(Auth) {
                scheme = "com.familyvault.app"
                host = "auth-callback"
                defaultExternalAuthAction = ExternalAuthAction.CustomTabs()
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
