package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class AppStateInfoViewModel(
    configSource: ConfigStatusSource
) : ViewModel() {
    private val _state =
        MutableStateFlow(AppStateInfoViewState(appVersion = configSource.appVersion))
    val state: StateFlow<AppStateInfoViewState> = _state.asStateFlow()

    init {
        configSource.isOnlineFlow.onEach { isOnline ->
            _state.update { it.copy(isOnline = isOnline.first, lastError = isOnline.second) }
        }.launchIn(viewModelScope)
        configSource.wideScreenFlow.onEach { isWide ->
            _state.update { it.copy(isWideLand = isWide) }
        }.launchIn(viewModelScope)
    }

    data class AppStateInfoViewState(
        val appVersion: String = "",
        val isWideLand: Boolean = false,
        val isDebug: Boolean = true,
        val isOnline: Boolean = false,
        val lastError: String? = null
    )
}