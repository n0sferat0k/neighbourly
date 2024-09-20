package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.LocalizationProgress
import com.neighbourly.app.d_entity.data.User
import kotlinx.coroutines.flow.Flow

interface SessionStore {
    suspend fun storeUser(user: User)

    suspend fun clear()

    suspend fun updateUser(updater: (User?) -> User?)

    suspend fun updateLocalization(updater: (LocalizationProgress) -> LocalizationProgress)

    val user: Flow<User?>
    val localization: Flow<LocalizationProgress>

    val isLoggedIn: Flow<Boolean>
    val token: String?
    val drawing: List<GpsItem>?
}
