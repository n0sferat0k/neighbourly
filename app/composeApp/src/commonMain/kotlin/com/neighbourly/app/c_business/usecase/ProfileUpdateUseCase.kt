package com.neighbourly.app.c_business.usecase

import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class ProfileUpdateUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun execute(file: MPFile<Any>) {
        sessionStore.token?.let {
            val user = apiGw.updateProfileImage(it, file)
        }
    }
}
