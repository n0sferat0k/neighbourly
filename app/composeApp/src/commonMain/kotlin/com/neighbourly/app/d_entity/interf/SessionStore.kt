package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.HeatmapItem
import com.neighbourly.app.d_entity.data.User
import kotlinx.coroutines.flow.Flow

interface SessionStore {
    suspend fun storeUser(user: User)

    suspend fun clear()

    suspend fun update(updater: (User?) -> User?)

    suspend fun storeHeatmap(heatmap: List<HeatmapItem>?)

    val user: Flow<User?>
    val heatmap: Flow<List<HeatmapItem>?>

    val isLoggedIn: Flow<Boolean>
    val token: String?
}
