package com.neighbourly.app.c_business.usecase.auth

import com.neighbourly.app.d_entity.data.Credentials
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class RegisterUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
        remember: Boolean,
    ) {
        val user = apiGw.register(username, password, fullname, email, phone)
        sessionStore.storeUser(user)
        sessionStore.storeCredentials(if (remember) Credentials(username, password) else null)
    }
}
