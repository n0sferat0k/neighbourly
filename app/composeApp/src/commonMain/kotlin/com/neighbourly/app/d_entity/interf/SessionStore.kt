package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.User
import kotlinx.coroutines.flow.Flow

interface SessionStore {
    suspend fun store(user: User)

    suspend fun clear()

    suspend fun update(updater: (User?) -> User?)

    val user: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    val token: String?
}
