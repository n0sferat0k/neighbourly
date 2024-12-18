package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import com.neighbourly.app.d_entity.interf.StatusUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

abstract class StatusMemoryStore : ConfigStatusSource, StatusUpdater {
    private val _state = MutableStateFlow(StatusMemoryStoreState())
    override val isOnlineFlow: Flow<Pair<Boolean, String?>> =
        _state.map { Pair(it.isOnline, it.lastError) }

    override fun setOnline(isOnline: Boolean, lastError: String?) {
        _state.update { it.copy(isOnline = isOnline, lastError = lastError) }
    }

    private data class StatusMemoryStoreState(
        val isOnline: Boolean = true,
        val lastError: String? = null
    )
}