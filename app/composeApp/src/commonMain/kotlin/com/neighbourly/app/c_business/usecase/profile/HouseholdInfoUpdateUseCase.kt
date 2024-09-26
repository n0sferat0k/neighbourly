package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdInfoUpdateUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        name: String,
        address: String,
        about: String,
    ) {
        val token = sessionStore.token

        token?.let {
            val user = apiGw.updateHousehold(it, name, address, about)
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
