package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.User

interface SessionStore {
    fun store(user: User)
}