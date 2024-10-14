package com.neighbourly.app.c_business.usecase.auth

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore

class LogoutUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
    val database: Db,
) {
    suspend fun execute(logoutAll: Boolean) {
        val token = sessionStore.token
        token?.let {
            kotlin.runCatching {
                apiGw.logout(token, logoutAll)
            }
        }
        sessionStore.clear()
        database.clear()
    }
}
