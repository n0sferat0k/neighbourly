package com.neighbourly.app.d_entity.interf

import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.neighbourly.app.d_entity.data.User

interface AuthApi {
    suspend fun login(
        username: String,
        password: String,
    ): User

    suspend fun register(
        username: String,
        password: String,
        fullName: String,
        email: String,
        phoneNumber: String,
    ): User

    suspend fun updateProfileImage(
        token: String,
        file: MPFile<Any>,
    )
}
