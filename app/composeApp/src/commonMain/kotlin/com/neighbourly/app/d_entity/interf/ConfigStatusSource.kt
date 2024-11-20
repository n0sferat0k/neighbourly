package com.neighbourly.app.d_entity.interf

import kotlinx.coroutines.flow.Flow

interface ConfigStatusSource {
    val wideScreenFlow: Flow<Boolean>
    val isOnlineFlow: Flow<Boolean>
}