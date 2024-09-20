package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdImageUpdateUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(profileImageFileContents: FileContents) {
        val token = sessionStore.token

        token?.let {
            apiGw.updateHouseholdImage(it, profileImageFileContents).let { imgUrl ->
                sessionStore.updateUser { it?.copy(household = it.household?.copy(imageurl = imgUrl)) }
            }
        }
    }
}
