package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdLocalizeUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun startMonitoring() {
        sessionStore.update { it?.copy(localizing = true) }
    }

    suspend fun stopMonitoring() {
        sessionStore.update { it?.copy(localizing = false) }
    }

    suspend fun reportLocation(
        latitude: Float,
        longitude: Float,
    ) {
        val token = sessionStore.token
        token?.let {
            apiGw.gpsLog(token, 3, latitude, longitude)
        }
    }

    suspend fun fetchGpsLogs() {
        val token = sessionStore.token
        token?.let {
            apiGw.getGpsHeatmap(token).let {
                sessionStore.storeHeatmap(it)
            }
        }
    }

    suspend fun fetchGpsCandidate() {
        val token = sessionStore.token
        token?.let {
            apiGw.getGpsCandidate(token).let {
                sessionStore.storeCandidate(it)
            }
        }
    }
}
