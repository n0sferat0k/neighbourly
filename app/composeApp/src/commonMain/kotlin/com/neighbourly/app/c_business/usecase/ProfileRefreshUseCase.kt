package com.neighbourly.app.c_business.usecase

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileRefreshUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute() {
        val token = sessionStore.token
        token?.let {
            val user = apiGw.refreshProfile(token)
            sessionStore.update { user.copy(authtoken = it?.authtoken) }
        }
    }
}
