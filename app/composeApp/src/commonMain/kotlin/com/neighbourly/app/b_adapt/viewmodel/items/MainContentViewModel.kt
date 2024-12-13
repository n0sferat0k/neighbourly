package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainContentViewModel(val contentSyncUseCase: ContentSyncUseCase, val sessionStore: SessionStore) : ViewModel() {
    private val _state = MutableStateFlow(MainContentViewState())
    val state: StateFlow<MainContentViewState> = _state.asStateFlow()

    init {
        if(sessionStore.user != null) {
            viewModelScope.launch {
                contentSyncUseCase.execute()
                _state.update { it.copy(landing = false) }
            }
        } else {
            _state.update { it.copy(landing = false) }
        }
        sessionStore.userFlow
            .onEach { user ->
                user?.let { _state.update { it.copy(householdId = user.household?.householdid) } }
            }.launchIn(viewModelScope)
    }

    data class MainContentViewState(
        val landing: Boolean = true,
        val householdId: Int? = null
    )
}