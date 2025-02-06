package com.neighbourly.app.a_device.store

import com.neighbourly.app.appVersionString
import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import com.neighbourly.app.d_entity.interf.StatusUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

abstract class StatusMemoryStore : ConfigStatusSource, StatusUpdater {
    private val _state = MutableStateFlow(StatusMemoryStoreState())

    override val appVersion: String
        get() = appVersionString

    override val isOnlineFlow: Flow<Pair<Boolean, String?>> =
        _state.map { Pair(it.isOnline, it.lastError) }

    override val isTokenExpFlow: Flow<Boolean>
        get() = _state.map { it.isTokenExp }

    override val isAiOnlineFlow: Flow<Boolean> =
        _state.map { it.isAiOnline }

    override val aiMessages: Flow<List<String>> = _state.map { it.aiMessages }

    override fun setOnline(isOnline: Boolean, isTokenExp: Boolean, lastError: String?) {
        _state.update { it.copy(isOnline = isOnline, isTokenExp = isTokenExp, lastError = lastError) }
    }

    override fun setAiOnline(isAiOnline: Boolean) {
        _state.update { it.copy(isAiOnline = isAiOnline) }
    }

    override fun storeAiMessage(message: String) {
        _state.update { it.copy(aiMessages = it.aiMessages + message) }
    }

    private data class StatusMemoryStoreState(
        val isOnline: Boolean = true,
        val isTokenExp: Boolean = false,
        val isAiOnline: Boolean = true,
        val lastError: String? = null,
        val aiMessages: List<String> = emptyList()
    )
}