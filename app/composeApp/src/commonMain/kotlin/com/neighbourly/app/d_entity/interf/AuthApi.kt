package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.User

interface AuthApi {
    suspend fun login(
        username: String,
        password: String,
    ): User

    suspend fun register(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
    ): User

    suspend fun updateProfileImage(
        token: String,
        profileImageFileContents: FileContents,
    ): String

    suspend fun refreshProfile(token: String): User

    suspend fun logout(
        token: String,
        logoutAll: Boolean,
    )

    suspend fun updateProfile(
        token: String,
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ): User
}
