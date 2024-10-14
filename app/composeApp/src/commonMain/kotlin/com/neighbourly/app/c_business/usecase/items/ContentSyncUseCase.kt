package com.neighbourly.app.c_business.usecase.items

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
        val token = sessionStore.token
        token?.let {
            val lastSyncTs = sessionStore.lastSyncTs
            if (force || (currentTs - (lastSyncTs ?: 0)) > SYNC_DEBOUNCE_S) {
                val lastModifTs = dbInteractor.getLastModifTs()
                val content = apiGw.synchronizeContent(token, lastModifTs)
                dbInteractor.storeItems(content.first)
                dbInteractor.storeUsers(content.second)
                dbInteractor.storeHouseholds(content.third)
                sessionStore.lastSyncTs = currentTs
            }
        }
    }
}