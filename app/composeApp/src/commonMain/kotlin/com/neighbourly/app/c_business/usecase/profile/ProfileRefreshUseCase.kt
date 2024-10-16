package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileRefreshUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute() {
        val token = sessionStore.user?.authtoken

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
