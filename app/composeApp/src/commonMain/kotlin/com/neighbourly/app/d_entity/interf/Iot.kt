package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.BoxStateUpdate
import kotlinx.coroutines.flow.Flow

interface Iot {
    val iotConnectedFlow :Flow<Boolean>
    val boxStateFlow: Flow<BoxStateUpdate>
    suspend fun monitorBoxes(boxIds: List<String>)
}