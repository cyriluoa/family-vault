package com.familyvault.app.core.di

import com.familyvault.app.data.auth.AuthRepositoryImpl
import com.familyvault.app.data.profile.ProfileRepositoryImpl
import com.familyvault.app.data.vault.VaultRepositoryImpl
import com.familyvault.app.domain.repository.AuthRepository
import com.familyvault.app.domain.repository.ProfileRepository
import com.familyvault.app.domain.repository.VaultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
    @Binds
    @Singleton
    abstract fun bindVaultRepository(
        vaultRepositoryImpl: VaultRepositoryImpl
    ): VaultRepository
}
