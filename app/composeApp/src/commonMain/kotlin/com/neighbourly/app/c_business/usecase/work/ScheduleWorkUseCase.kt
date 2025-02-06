package com.neighbourly.app.c_business.usecase.work

import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.ScheduledWork
import com.neighbourly.app.d_entity.data.ScheduledWorkType.REMIND
import com.neighbourly.app.d_entity.data.ScheduledWorkType.SYNC
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.requestFutureWork
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

class ScheduleWorkUseCase(
    val sessionStore: SessionStore,
    val database: Db
) {

    suspend fun execute() {
        val now = (System.currentTimeMillis() / 1000).toInt()
        val nextRefresh = sessionStore.user?.lastSyncTs?.plus(REFRESH_PERIOD_SECONDS)

        var nextReminderId: Int? = null
        var nextTimestamp: Int = Int.MAX_VALUE
        database.filterItems(ItemType.REMINDER).forEach { item ->
            item.description?.takeIf { it.isNotEmpty() }?.split(",")?.map { it.toInt() }?.sorted()
                ?.firstOrNull { it > now }
                ?.let { time ->
                    if (nextTimestamp > time) {
                        nextReminderId = item.id
                        nextTimestamp = time
                    }
                }
        }
        val work =
            if (nextRefresh != null && nextRefresh < nextTimestamp) {
                ScheduledWork(
                    delaySeconds = nextTimestamp - now,
                    type = SYNC,
                )
            } else if (nextReminderId != null) {
                ScheduledWork(
                    delaySeconds = nextTimestamp - now,
                    type = REMIND,
                    id = nextReminderId,
                )
            } else null

        work?.let { requestFutureWork(it) }
    }

    companion object {
        val REFRESH_PERIOD_SECONDS = SECONDS.convert(10, MINUTES)
    }
}