package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.postSystemNotification

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
            val lastSyncTs = sessionStore.lastSyncTs ?: 0
            if(lastSyncTs == 0) {
                dbInteractor.clear()
            }
            if (force || (currentTs - lastSyncTs) > SYNC_DEBOUNCE_S) {
                val lastModifTs = dbInteractor.getLastModifTs()
                val knownIds =  dbInteractor.getItemIds()
                val syncData = apiGw.synchronizeContent(token, lastModifTs)
                val newIds = syncData.itemIds.filter { !knownIds.contains(it) }

                dbInteractor.storeItems(syncData.items)
                dbInteractor.storeUsers(syncData.users)
                dbInteractor.storeHouseholds(syncData.houses)

                dbInteractor.stripItems(syncData.itemIds)
                dbInteractor.stripUsers(syncData.userIds)
                dbInteractor.stripHouseholds(syncData.houseIds)

                if(lastSyncTs  > 0 && newIds.isNotEmpty()) {
                    val newItems = syncData.items.filter{newIds.contains(it.id)}
                    val newItemsHouseholds = dbInteractor.filterHouseholds(newItems.map { it.householdId }.filterNotNull())

                    newItems.forEach { item ->
                        val houseName = newItemsHouseholds.firstOrNull{it.householdid == item.householdId}?.name.orEmpty()
                        postSystemNotification(item.id ?: 0, houseName, item.name.orEmpty())
                    }
                }

                sessionStore.lastSyncTs = currentTs
            }
        }
    }
}