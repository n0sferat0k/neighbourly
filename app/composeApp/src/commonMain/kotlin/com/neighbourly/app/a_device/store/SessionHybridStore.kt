package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.Credentials
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.LocalizationProgress
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionHybridStore(
    val keyValueRegistry: KeyValueRegistry,
) : SessionStore {
    private lateinit var userState: MutableStateFlow<User?>
    override val userFlow by lazy { userState.asSharedFlow() }
    override val isLoggedInFlow by lazy { userFlow.map { it != null } }
    override val user: User?
        get() = userState.value

    override var lastSyncTs: Int?
        get() = userState.value?.lastSyncTs
        set(value) {
            userState.update { it?.copy(lastSyncTs = value) }
            saveToStore()
        }

    private var localizationState = MutableStateFlow(LocalizationProgress())
    override val localizationFlow = localizationState.asSharedFlow()
    override val drawing: List<GpsItem>?
        get() = localizationState.value.drawingPoints

    private var _credentials: Credentials? = null
    override val credentials: Credentials? get() = _credentials?.copy()

    init {
        loadRememberedCredentialsFromStore()

        if (STORE_VERSION == keyValueRegistry.getString(KEY_STORE_VERSION)) {
            loadUserFromStore()
        } else {
            userState = MutableStateFlow(null)
            clear()
        }
    }

    override suspend fun storeUser(user: User) {
        userState.emit(user)
        saveToStore()
    }

    override suspend fun storeCredentials(credentials: Credentials?) {
        this._credentials = credentials

        if (credentials != null) {
            keyValueRegistry.putString(KEY_REM_USER, credentials.username)
            keyValueRegistry.putString(KEY_REM_PASS, credentials.password)
        } else {
            keyValueRegistry.remove(KEY_REM_USER)
            keyValueRegistry.remove(KEY_REM_PASS)
        }
    }

    override suspend fun updateUser(updater: (User?) -> User?) {
        userState.emit(updater(userState.value))
        saveToStore()
    }

    override suspend fun updateLocalization(updater: (LocalizationProgress) -> LocalizationProgress) {
        localizationState.emit(updater(localizationState.value))
    }

    override fun clear() {
        userState.tryEmit(null)
        localizationState.tryEmit(LocalizationProgress())
        saveToStore()
    }

    override fun muteHousehold(householdId: Int, mute: Boolean) {
        userState.update {
            it?.copy(
                mutedHouseholds = (
                        if (mute)
                            it.mutedHouseholds + householdId
                        else
                            it.mutedHouseholds.filter { it != householdId }
                        )
                    .toSet()
            )
        }
        saveToStore()
    }

    override fun mutePerson(personId: Int, mute: Boolean) {
        userState.update {
            it?.copy(
                mutedUsers = (
                        if (mute)
                            it.mutedUsers + personId
                        else
                            it.mutedUsers.filter { it != personId }
                        )
                    .toSet()
            )
        }
        saveToStore()
    }

    override fun watchItem(itemId: Int, watch: Boolean) {
        userState.update {
            it?.copy(
                watchedItems = (
                        if (watch)
                            it.watchedItems + itemId
                        else
                            it.watchedItems.filter { it != itemId }
                        )
                    .toSet()
            )
        }
        saveToStore()
    }

    private fun loadUserFromStore() {
        userState = MutableStateFlow(
            keyValueRegistry.getString(KEY_USER)?.let {
                Json.decodeFromString<StoreUser>(it).toUser()
            }
        )
    }

    private fun loadRememberedCredentialsFromStore() {
        keyValueRegistry.let {
            it.getString(KEY_REM_USER)?.let { user ->
                it.getString(KEY_REM_PASS)?.let { pass ->
                    _credentials = Credentials(user, pass)
                }
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
        const val STORE_VERSION = "2"
        const val KEY_STORE_VERSION = "key.store.version"
        const val KEY_USER = "key.user"
        const val KEY_REM_USER = "key.remembered.user"
        const val KEY_REM_PASS = "key.remembered.pass"
    }
}
