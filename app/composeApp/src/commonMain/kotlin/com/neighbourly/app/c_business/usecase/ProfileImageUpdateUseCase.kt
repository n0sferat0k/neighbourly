package com.neighbourly.app.c_business.usecase

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileImageUpdateUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(profileImageFileContents: FileContents) {
        val token = sessionStore.token

        token?.let {
            apiGw.updateProfileImage(it, profileImageFileContents).let { imgUrl ->
                sessionStore.update { it?.copy(imageurl = imgUrl) }
            }
        }
    }
}
