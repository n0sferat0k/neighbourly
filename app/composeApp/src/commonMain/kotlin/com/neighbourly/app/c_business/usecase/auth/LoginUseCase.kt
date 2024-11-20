package com.neighbourly.app.c_business.usecase.auth

import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.d_entity.data.Credentials
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class LoginUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
    val syncUseCase: ContentSyncUseCase,
) {
    suspend fun execute(
        username: String,
        password: String,
        remember: Boolean,
    ) {
        val user = apiGw.login(username, password)

        sessionStore.storeUser(user)
        sessionStore.storeCredentials(if (remember) Credentials(username, password) else null)

        syncUseCase.execute(true)
    }
}
