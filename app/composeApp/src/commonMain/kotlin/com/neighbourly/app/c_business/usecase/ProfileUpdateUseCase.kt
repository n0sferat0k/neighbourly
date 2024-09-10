package com.neighbourly.app.c_business.usecase

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileUpdateUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ) {
        val token = sessionStore.token

        token?.let {
            val user = apiGw.updateProfile(it, fullname, email, phone, about)
            sessionStore.store(user)
        }
    }
}
