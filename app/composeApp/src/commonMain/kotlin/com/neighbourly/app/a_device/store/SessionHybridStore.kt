package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.LocalizationProgress
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionHybridStore(
    val keyValueRegistry: KeyValueRegistry,
) : SessionStore {
    private var userState = MutableStateFlow<User?>(null)
    private var localizationState = MutableStateFlow(LocalizationProgress())

    override val userFlow = userState.asSharedFlow()
    override val localizationFlow = localizationState.asSharedFlow()
    override val isLoggedInFlow = userFlow.map { it != null }

    override val user: User?
        get() = userState.value
    override val drawing: List<GpsItem>?
        get() = localizationState.value.drawingPoints
    override var lastSyncTs: Int?
        get() = userState.value?.lastSyncTs
        set(value) {
            userState.update { it?.copy(lastSyncTs = value) }
            saveToStore()
        }

    init {
        if (STORE_VERSION == keyValueRegistry.getString(KEY_STORE_VERSION)) {
            MainScope().launch {
                loadFromStore()
            }
        }
    }

    override suspend fun storeUser(user: User) {
        userState.emit(user)
        saveToStore()
    }

    override suspend fun updateUser(updater: (User?) -> User?) {
        userState.emit(updater(userState.value))
        saveToStore()
    }

    override suspend fun updateLocalization(updater: (LocalizationProgress) -> LocalizationProgress) {
        localizationState.emit(updater(localizationState.value))
    }

    override suspend fun clear() {
        userState.emit(null)
        localizationState.emit(LocalizationProgress())
        saveToStore()
    }

    private suspend fun loadFromStore() {
        withContext(Dispatchers.IO) {
            keyValueRegistry.getString(KEY_USER)?.let {
                userState.emit(Json.decodeFromString<StoreUser>(it).toUser())
            }
        }
    }

    private fun saveToStore() {
        keyValueRegistry.putString(KEY_STORE_VERSION, STORE_VERSION)
        userState.value?.let {
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
