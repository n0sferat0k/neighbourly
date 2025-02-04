package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.Credentials
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.LocalizationProgress
import com.neighbourly.app.d_entity.data.User
import kotlinx.coroutines.flow.Flow

interface SessionStore {
    suspend fun storeUser(user: User)

    suspend fun storeCredentials(credentials: Credentials? = null)

    fun clear()

    suspend fun updateUser(updater: (User?) -> User?)

    suspend fun updateLocalization(updater: (LocalizationProgress) -> LocalizationProgress)

    val userFlow: Flow<User?>
    val localizationFlow: Flow<LocalizationProgress>

    val user: User?
    val credentials: Credentials?

    val isLoggedInFlow: Flow<Boolean>
    var lastSyncTs : Int?
    val drawing: List<GpsItem>?

    fun muteHousehold(householdId: Int, mute: Boolean)
    fun mutePerson(personId: Int, mute: Boolean)
}
