package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.SessionStore

class SessionMemoryStore : SessionStore {
    private var user: User? = null
    override fun store(user: User) {
        this.user = user
    }
}