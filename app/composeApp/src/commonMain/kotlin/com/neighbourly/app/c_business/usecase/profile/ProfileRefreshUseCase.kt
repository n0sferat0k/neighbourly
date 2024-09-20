package com.neighbourly.app.c_business.usecase.profile

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
            sessionStore.updateUser {
                it?.copy(
                    username = user.username,
                    about = user.about,
                    fullname = user.fullname,
                    email = user.email,
                    phone = user.phone,
                    imageurl = user.imageurl,
                    household = user.household,
                    neighbourhoods = user.neighbourhoods,
                )
            }
        }
    }
}
