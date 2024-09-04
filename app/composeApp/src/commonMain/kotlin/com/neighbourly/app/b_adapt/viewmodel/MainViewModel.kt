package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MainViewModel(
    sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(MainViewState())
    val state: StateFlow<MainViewState> = _state.asStateFlow()

    init {
        sessionStore.isLoggedIn
            .onEach { isLoggedIn ->
                _state.update { it.copy(isLoggedIn = isLoggedIn) }
            }.launchIn(viewModelScope)
    }

    data class MainViewState(
        val isLoggedIn: Boolean = false,
    )
}
