package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore

const val SYNC_DEBOUNCE_S = 300

class ContentSyncUseCase(
    val dbInteractor: Db,
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    private val currentTs
        get() = (System.currentTimeMillis() / 1000).toInt()

    suspend fun execute(force: Boolean = false) {
        val token = sessionStore.user?.authtoken

        token?.let {
            val lastSyncTs = sessionStore.lastSyncTs
            if (force || (currentTs - (lastSyncTs ?: 0)) > SYNC_DEBOUNCE_S) {
                val lastModifTs = dbInteractor.getLastModifTs()
                val syncData = apiGw.synchronizeContent(token, lastModifTs)
                dbInteractor.storeItems(syncData.items)
                dbInteractor.storeUsers(syncData.users)
                dbInteractor.storeHouseholds(syncData.houses)

                dbInteractor.stripItems(syncData.itemIds)
                dbInteractor.stripUsers(syncData.userIds)
                dbInteractor.stripHouseholds(syncData.houseIds)

                sessionStore.lastSyncTs = currentTs
            }
        }
    }
}