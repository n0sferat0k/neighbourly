package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class SessionMemoryStore : SessionStore {
    private var state = MutableStateFlow<User?>(null)

    override val user = state.asSharedFlow()
    override val isLoggedIn = user.map { it != null }

    override suspend fun store(user: User) {
        state.emit(user)
    }

    override suspend fun clear() {
        state.emit(null)
    }

    override val token: String? = state.value?.authtoken
}
