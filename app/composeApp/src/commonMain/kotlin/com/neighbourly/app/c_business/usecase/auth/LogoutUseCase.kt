package com.neighbourly.app.c_business.usecase.auth

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.GpsTracker
import com.neighbourly.app.d_entity.interf.SessionStore

class LogoutUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
    val gpsTracker: GpsTracker,
) {
    suspend fun execute(logoutAll: Boolean) {
        val token = sessionStore.token
        token?.let {
            gpsTracker.stopTracking()
            kotlin.runCatching {
                apiGw.logout(token, logoutAll)
            }
            sessionStore.clear()
        }
    }
}
