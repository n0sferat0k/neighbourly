package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.User

interface AuthApi {
    suspend fun login(username: String, password: String): User
}