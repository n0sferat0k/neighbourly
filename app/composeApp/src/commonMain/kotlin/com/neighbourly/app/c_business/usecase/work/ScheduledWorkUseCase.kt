package com.neighbourly.app.c_business.usecase.work

import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.postSystemNotification
import com.neighbourly.app.requestFutureWork

class ScheduledWorkUseCase(val dbInteractor: Db) {

    suspend fun scheduleNextWork() {
        val now = (System.currentTimeMillis() / 1000).toInt()
        var nextReminder: Item? = null
        var nextTimestamp: Int = Int.MAX_VALUE
        dbInteractor.filterItems(ItemType.REMINDER).forEach { item ->
            item.description?.split(",")?.map { it.toInt() }?.sorted()?.firstOrNull { it > now }?.let { time->
                if(nextTimestamp > time) {
                    nextReminder = item
                    nextTimestamp = time
                }
            }
        }
        nextReminder?.id?.let {
            requestFutureWork(nextTimestamp - now, mapOf(CMD to CMD_REMIND, ITEM_ID to it))
        }
    }

    suspend fun handle(data: Map<String, Any>) {
        when(data[CMD])  {
            CMD_REMIND -> {
                val item = dbInteractor.getItem(data[ITEM_ID] as Int)
                postSystemNotification(item.id ?: 0, item.name.orEmpty(), item.name.orEmpty())
            }
        }

        scheduleNextWork()
    }

    companion object {
        const val CMD = "CMD"
        const val CMD_REMIND = "CMD_REMIND"
        const val ITEM_ID =  "ITEM_ID"
    }
}