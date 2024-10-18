package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileImageUpdateUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(profileImageFileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.updateProfileImage(it, profileImageFileContents).let { imgUrl ->
                sessionStore.updateUser { it?.copy(imageurl = imgUrl) }
            }
        }
    }
}
