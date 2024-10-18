package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class FetchProfileUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        id: Int,
        username: String,
    ): User? {
        val token = sessionStore.user?.authtoken

        return token?.let {
            apiGw.fetchProfile(token, id, username)
        }
    }
}
