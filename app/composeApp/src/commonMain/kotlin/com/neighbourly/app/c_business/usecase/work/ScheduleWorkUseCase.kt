package com.neighbourly.app.c_business.usecase.work

import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.ScheduledWork
import com.neighbourly.app.d_entity.data.ScheduledWorkType.REMIND
import com.neighbourly.app.d_entity.data.ScheduledWorkType.SYNC
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.requestFutureWork
import kotlinx.datetime.Clock
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.math.max

class ScheduleWorkUseCase(
    val sessionStore: SessionStore,
    val database: Db
) {

    suspend fun execute() {
        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA scheduling next work")
        val now = Clock.System.now().epochSeconds.toInt()
        val nextSyncTimestamp = sessionStore.user?.lastSyncTs?.plus(REFRESH_PERIOD_SECONDS)

        var nextReminderId: Int? = null
        var nextReminderTimestamp: Int = Int.MAX_VALUE
        database.filterItems(ItemType.REMINDER).forEach { item ->
            item.description?.takeIf { it.isNotEmpty() }?.split(",")?.map { it.toInt() }?.sorted()
                ?.firstOrNull { it > now }
                ?.let { time ->
                    if (nextReminderTimestamp > time) {
                        nextReminderId = item.id
                        nextReminderTimestamp = time
                    }
                }
        }
        val work =
            if (nextSyncTimestamp != null && nextSyncTimestamp < nextReminderTimestamp) {
                ScheduledWork(
                    delaySeconds = max(0, nextSyncTimestamp - now),
                    type = SYNC,
                )
            } else if (nextReminderId != null) {
                ScheduledWork(
                    delaySeconds = max(0, nextReminderTimestamp - now),
                    type = REMIND,
                    id = nextReminderId,
                )
            } else null

        work?.let {
            requestFutureWork(it)
            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA next (${it.type}) work in ${it.delaySeconds} s")
        } ?: run {
            println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA next work not scheduled")
        }
    }

    companion object {
        val REFRESH_PERIOD_SECONDS = SECONDS.convert(10, MINUTES).toInt()
    }
}