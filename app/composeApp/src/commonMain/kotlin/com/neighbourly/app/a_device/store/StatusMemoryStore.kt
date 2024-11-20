package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import com.neighbourly.app.d_entity.interf.StatusUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

abstract class StatusMemoryStore : ConfigStatusSource, StatusUpdater {
    private val _state = MutableStateFlow(StatusMemoryStoreState())
    override val isOnlineFlow: Flow<Boolean> = _state.map { it.isOnline }

    override fun setOnline(isOnline: Boolean) {
        _state.update { it.copy(isOnline = isOnline) }
    }


    private data class StatusMemoryStoreState(val isOnline: Boolean = true)
}