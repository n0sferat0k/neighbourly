package com.neighbourly.app.d_entity.interf

import kotlinx.coroutines.flow.Flow

interface ConfigStatusSource {
    val wideScreenFlow: Flow<Boolean>
    val isOnlineFlow: Flow<Pair<Boolean,String?>>
    val isAiOnlineFlow: Flow<Boolean>
    val aiMessages: Flow<List<String>>
    val appVersion: String
}