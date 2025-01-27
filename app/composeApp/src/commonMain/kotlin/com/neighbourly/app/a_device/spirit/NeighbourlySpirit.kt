package com.neighbourly.app.a_device.spirit

import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.gateway.api.toItemDTO
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.work.ScheduleWorkUseCase
import com.neighbourly.app.d_entity.data.ScheduledWork
import com.neighbourly.app.d_entity.data.ScheduledWorkType
import com.neighbourly.app.d_entity.interf.AI
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.interf.Summonable
import com.neighbourly.app.postSystemNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NeighbourlySpirit : Summonable {
    val scheduledWorkUseCase: ScheduleWorkUseCase by lazy { KoinProvider.KOIN.get() }
    val contentSyncUseCase: ContentSyncUseCase by lazy { KoinProvider.KOIN.get() }
    val database: Db by lazy { KoinProvider.KOIN.get() }
    val sessionStore: SessionStore by lazy { KoinProvider.KOIN.get() }
    val aiGw: AI by lazy { KoinProvider.KOIN.get() }

    private val spiritScope = CoroutineScope(Dispatchers.Default)

    override fun summonOnProfileSyncComplete() {
        spiritScope.launch {
            scheduledWorkUseCase.execute()
        }
    }

    override fun summonOnItemOp() {
        spiritScope.launch {
            scheduledWorkUseCase.execute()
        }
    }

    override fun summonOnContentSyncComplete(
        lastSyncTs: Int,
        novelItemIds: List<Int>,
        syncedItemIds: List<Int>,
        syncedUserIds: List<Int>,
        syncedHouseIds: List<Int>
    ) {
        spiritScope.launch {
            if (lastSyncTs > 0 && (novelItemIds + syncedItemIds).isNotEmpty()) {
                val novelItems =
                    database.filterItems(ids = novelItemIds + syncedItemIds)
                val novelItemsHouseholds =
                    database.filterHouseholds(novelItems.map { it.householdId }.filterNotNull())
                val myNeighbourhoods = sessionStore.user?.neighbourhoods.orEmpty()

                val text = kotlin.runCatching {
                    aiGw.generate(
                        system = "You are a helpful assistant, your speach is short and concise, you live inside an app used to connect people and households inside neighbourhoods, you will generate descriptions of items posted to the app by its users.",
                        prompt = "Here is a set or recent items: " + Json.encodeToString(
                            novelItems.map { it.toItemDTO() }
                        )
                    )
                }.getOrDefault(
                    novelItems.map { item ->
                        val house =
                            novelItemsHouseholds.firstOrNull { it.householdid == item.householdId }
                        val neighbourhood =
                            myNeighbourhoods.firstOrNull { it.neighbourhoodid == item.neighbourhoodId }

                        neighbourhood?.name + "/" + house?.name.orEmpty() + ": " + item.name
                    }.joinToString("\r\n")
                )

                postSystemNotification(
                    id = (novelItemIds + syncedItemIds).joinToString(","),
                    text = text
                )
            }
            scheduledWorkUseCase.execute()
        }
    }

    override fun summonOnScheduledWork(work: ScheduledWork) {
        spiritScope.launch {
            when (work.type) {
                ScheduledWorkType.REMIND -> {
                    work.id?.let { database.getItem(it) }?.let { item ->
                        val neighbourhood =
                            sessionStore.user?.neighbourhoods?.firstOrNull { it.neighbourhoodid == item.neighbourhoodId }

                        postSystemNotification(
                            id = item.id.toString(),
                            title = neighbourhood?.name.orEmpty(),
                            text = item.name.orEmpty()
                        )
                    }
                    scheduledWorkUseCase.execute()
                }

                ScheduledWorkType.SYNC -> contentSyncUseCase.execute(force = true)
            }
        }
    }
}