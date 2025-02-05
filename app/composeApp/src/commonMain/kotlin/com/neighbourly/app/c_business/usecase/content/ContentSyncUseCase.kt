package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.interf.Summonable

const val SYNC_DEBOUNCE_S = 300

class ContentSyncUseCase(
    val database: Db,
    val apiGw: Api,
    val sessionStore: SessionStore,
    val summonable: Summonable
) {
    private val currentTs
        get() = (System.currentTimeMillis() / 1000).toInt()

    suspend fun execute(force: Boolean = false) {
        val token = sessionStore.user?.authtoken

        token?.let {
            val lastSyncTs = sessionStore.lastSyncTs ?: 0
            if (lastSyncTs == 0) {
                database.clear()
            }
            if (force || (currentTs - lastSyncTs) > SYNC_DEBOUNCE_S) {

                //first sync all item modifications
                val lastModifTs = database.getLastModifTs()
                val knownIds = database.getItemIds()
                val syncData = apiGw.synchronizeContent(token, lastModifTs)

                database.storeItems(syncData.items)
                database.storeUsers(syncData.users)
                database.storeHouseholds(syncData.houses)

                database.stripItems(syncData.itemIds)
                database.stripUsers(syncData.userIds)
                database.stripHouseholds(syncData.houseIds)

                //next sync messages for owned items only
                val ownedItemIds =
                    database.filterItems(householdId = sessionStore.user?.householdid).map { it.id }
                        .filterNotNull()
                val watchedItems = sessionStore.user?.watchedItems.orEmpty()

                val newMessages = apiGw.getItemsMessages(token, ownedItemIds + watchedItems, sessionStore.lastSyncTs)

                summonable.summonOnContentSyncComplete(
                    lastSyncTs,
                    syncData.itemIds.filter { !knownIds.contains(it) },
                    syncData.items.map { it.id }.filterNotNull(),
                    syncData.users.map { it.id },
                    syncData.houses.map { it.householdid },
                    newMessages
                )

                sessionStore.lastSyncTs = currentTs
            }
        }
    }

}