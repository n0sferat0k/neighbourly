package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdMemberAddUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        id: Int,
        username: String,
    ) {
        val token = sessionStore.token

        token?.let {
            val user = apiGw.addMemberToHousehold(it, id, username)
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
