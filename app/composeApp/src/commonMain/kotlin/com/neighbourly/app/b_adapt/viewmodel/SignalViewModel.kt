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

class SignalViewModel(
    configSource: ConfigStatusSource
) : ViewModel() {
    private val _state = MutableStateFlow(SignalViewState())
    val state: StateFlow<SignalViewState> = _state.asStateFlow()

    init {
        configSource.isOnlineFlow.onEach { isOnline ->
            _state.update { it.copy(isOnline = isOnline) }
        }.launchIn(viewModelScope)
    }

    data class SignalViewState(val isOnline: Boolean = false)
}