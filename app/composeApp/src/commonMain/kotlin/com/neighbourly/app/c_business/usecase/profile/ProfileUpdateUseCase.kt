package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileUpdateUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ) {
        val token = sessionStore.user?.authtoken

        token?.let {
            val user = apiGw.updateProfile(it, fullname, email, phone, about)
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
