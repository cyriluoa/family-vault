package com.familyvault.app.domain.repository

import com.familyvault.app.domain.model.AppProfile

interface ProfileRepository {
    suspend fun getCurrentProfile(): AppProfile?
}