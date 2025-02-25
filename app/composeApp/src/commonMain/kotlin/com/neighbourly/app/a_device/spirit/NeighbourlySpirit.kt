package com.neighbourly.app.a_device.spirit

import com.neighbourly.app.KoinProvider
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.work.ScheduleWorkUseCase
import com.neighbourly.app.d_entity.data.AiVariant
import com.neighbourly.app.d_entity.data.ItemMessage
import com.neighbourly.app.d_entity.data.ItemType
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
        syncedHouseIds: List<Int>,
        newMessagesOfInterest: List<ItemMessage>,
    ) {
        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Reacting to sync complete")
        spiritScope.launch {
            //ignore first sync
            if (lastSyncTs == 0) return@launch

            val muteHouseIds = sessionStore.user?.mutedHouseholds.orEmpty()
            val mutePersonIds = sessionStore.user?.mutedUsers.orEmpty()

            //react to new messages
            val messagesFromOthers =
                newMessagesOfInterest.filter { it.userId != sessionStore.user?.id }
            if (messagesFromOthers.isNotEmpty()) {
                val messagePeople = database.getUsers(ids = messagesFromOthers.map { it.userId }
                    .filter { !mutePersonIds.contains(it) }.filterNotNull())
                val messageItems = database.filterItems(ids = messagesFromOthers.map { it.itemId }
                    .filterNotNull())

                val messageDesc = messagesFromOthers.map { message ->
                    val person = messagePeople.firstOrNull { it.id == message.userId }
                    val item = messageItems.firstOrNull { it.id == message.itemId }
                    item?.let {
                        person?.let {
                            message.itemId to "${person.fullname ?: person.username} commented on '${item.name}' : ${message.message}"
                        }
                    }
                }.filterNotNull().toMap()

                if (messageDesc.size > 0) {
                    kotlin.runCatching {
                        val messageDescStr = messageDesc.values.joinToString(";")
                        aiGw.generate(
                            aiVariant = AiVariant.AiVariantGemini(apiKey = "AIzaSyB4Yix2w3QUpVKRLrI2Bfckd30XbGriYPg"),
                            system = """You are a helpful assistant, your speach is short and concise.
                                        You will provide a short summary of messages posted by people on items of households in a neighbourhood.
                                        You will return only the summary, no formatting, no other text, and no longer than 2 sentences!""".trimIndent(),
                            prompt = "Here are the messages: $messageDescStr"
                        )
                    }.getOrNull()?.let { aiSummary ->
                        postSystemNotification(
                            id = (messageDesc.keys).joinToString(","),
                            text = aiSummary
                        )
                    } ?: run {
                        for (entry in messageDesc) {
                            postSystemNotification(
                                id = entry.key.toString(),
                                text = entry.value
                            )
                        }
                    }
                }
            }

            //react to new/updated items
            val ids = novelItemIds + syncedItemIds
            if (ids.isNotEmpty()) {
                val items = database.filterItems(ids = ids)
                    .filter { it.type != ItemType.REMINDER } //reminders show only when they are set, no need to notify of new reminders

                val itemHouses = database.filterHouseholds(items.map { it.householdId }
                    .filter { !muteHouseIds.contains(it) }.filterNotNull())
                val itemPeople =
                    database.getUsers(items.map { it.userId }.filter { !mutePersonIds.contains(it) }
                        .filterNotNull())

                val itemsDesc = items.map { item ->
                    val person = itemPeople.firstOrNull { it.id == item.userId }
                    val house = itemHouses.firstOrNull { it.householdid == item.householdId }
                    house?.let {
                        person?.let {
                            item.id to "${person.fullname ?: person.username} from ${house.name} at ${house.address} posted a ${item.type.name} : ${item.name} - ${item.description}"
                        }
                    }
                }.filterNotNull().toMap()

                if (itemsDesc.size > 0) {
                    kotlin.runCatching {
                        val itemDescStr = itemsDesc.values.joinToString(";")
                        aiGw.generate(
                            aiVariant = AiVariant.AiVariantGemini(apiKey = "AIzaSyB4Yix2w3QUpVKRLrI2Bfckd30XbGriYPg"),
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
            }
            scheduledWorkUseCase.execute()
        }
    }

    override fun summonOnScheduledWork(work: ScheduledWork) {
        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Handling scheduled work: ${work.type}")
        spiritScope.launch {
            kotlin.runCatching {
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
                    }

                    ScheduledWorkType.SYNC -> contentSyncUseCase.execute(force = true)
                }
            }
            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Done Handling scheduled work: ${work.type}")
            scheduledWorkUseCase.execute()
        }
    }
}