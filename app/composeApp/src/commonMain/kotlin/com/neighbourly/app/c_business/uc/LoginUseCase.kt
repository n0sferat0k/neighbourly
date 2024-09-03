package com.neighbourly.app.c_business.uc

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class LoginUseCase(val apiGw: AuthApi, val sessionStore: SessionStore) {

    suspend fun execute(username: String, password: String) {
        val user = apiGw.login(username, password)
        sessionStore.store(user)
    }
}