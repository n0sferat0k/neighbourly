package com.neighbourly.app.c_business.usecase

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class LogoutUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(logoutAll: Boolean) {
        val token = sessionStore.token
        token?.let {
            try {
                apiGw.logout(token, logoutAll)
            } finally {
                sessionStore.clear()
            }
        }
    }
}