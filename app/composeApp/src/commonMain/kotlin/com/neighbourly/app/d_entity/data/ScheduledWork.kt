package com.neighbourly.app.d_entity.data

import kotlinx.serialization.Serializable

@Serializable
data class ScheduledWork(
    val delaySeconds: Int = 0,
    val type: ScheduledWorkType = ScheduledWorkType.SYNC,
    val id: Int? = null
)

enum class ScheduledWorkType() {
    REMIND, SYNC
}