package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.ItemMessage
import com.neighbourly.app.d_entity.data.ScheduledWork

interface Summonable {
    fun summonOnScheduledWork(work: ScheduledWork)
    fun summonOnProfileSyncComplete()
    fun summonOnContentSyncComplete(
        lastSyncTs: Int,
        novelItemIds: List<Int>,
        syncedItemIds: List<Int>,
        syncedUserIds: List<Int>,
        syncedHouseIds: List<Int>,
        newMessages: List<ItemMessage>
    )

    fun summonOnItemOp()
}