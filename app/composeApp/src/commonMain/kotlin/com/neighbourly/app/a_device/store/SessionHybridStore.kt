package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionHybridStore(
    val keyValueRegistry: KeyValueRegistry,
) : SessionStore {
    private var state = MutableStateFlow<User?>(null)

    override val user = state.asSharedFlow()
    override val isLoggedIn = user.map { it != null }

    init {
        if (STORE_VERSION == keyValueRegistry.getString(KEY_STORE_VERSION)) {
            MainScope().launch {
                loadFromStore()
            }
        }
    }

    override suspend fun store(user: User) {
        state.emit(user)
        saveToStore()
    }

    override suspend fun clear() {
        state.emit(null)
        saveToStore()
    }

    override val token: String?
        get() = state.value?.authtoken

    private suspend fun loadFromStore() {
        withContext(Dispatchers.IO) {
            keyValueRegistry.getString(KEY_USER)?.let {
                state.emit(Json.decodeFromString<StoreUser>(it).toUser())
            }
        }
    }

    private suspend fun saveToStore() {
        keyValueRegistry.putString(KEY_STORE_VERSION, STORE_VERSION)
        state.value?.let {
            keyValueRegistry.putString(KEY_USER, Json.encodeToString(it.toStoreUser()))
        } ?: run {
            keyValueRegistry.remove(KEY_USER)
        }
    }

    companion object {
        const val STORE_VERSION = "1"
        const val KEY_STORE_VERSION = "key.store.version"
        const val KEY_USER = "key.user"
    }
}
