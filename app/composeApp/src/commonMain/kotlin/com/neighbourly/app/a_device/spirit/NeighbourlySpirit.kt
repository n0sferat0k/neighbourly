package com.neighbourly.app.a_device.spirit

import com.neighbourly.app.KoinProvider
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
            val ids = novelItemIds + syncedItemIds
            if (lastSyncTs > 0 && ids.isNotEmpty()) {
                val items = database.filterItems(ids = ids)
                val itemHouses =
                    database.filterHouseholds(items.map { it.householdId }.filterNotNull())
                val itemPeople = database.getUsers(items.map { it.userId }.filterNotNull())
                val itemsDesc = items.map { item ->
                    val person = itemPeople.firstOrNull { it.id == item.userId }
                    val house = itemHouses.firstOrNull { it.householdid == item.householdId }
                    item.id to "${person?.fullname ?: person?.username} from ${house?.name} at ${house?.address} posted a ${item.type.name} : ${item.name} - ${item.description}"
                }.toMap()

                kotlin.runCatching {
                    val itemDescStr = itemsDesc.values.joinToString(";")
                    aiGw.generate(
                        system = """You are a helpful assistant, your speach is short and concise.
                                    You will provide a short summary of items posted by people in households of a neighbourhood.
                                    You will return only the summary, no formatting, no other text, and no longer than 2 sentences!""".trimIndent(),
                        prompt = "Here are the items: $itemDescStr"
                    )
                }.getOrNull()?.let { aiSummary ->
                    postSystemNotification(
                        id = (ids).joinToString(","),
                        text = aiSummary
                    )
                } ?: run {
                    for (entry in itemsDesc) {
                        postSystemNotification(
                            id = entry.key.toString(),
                            text = entry.value
                        )
                    }
                }
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