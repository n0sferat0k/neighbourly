package com.neighbourly.app.c_business.usecase

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class RegisterUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
    ) {
        val user = apiGw.register(username, password, fullname, email, phone)
        sessionStore.store(user)
    }
}