package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStateInfoViewModel(
    LogoutUseCase: LogoutUseCase,
    contentSyncUseCase: ContentSyncUseCase,
    configSource: ConfigStatusSource,
    sessionStore: SessionStore,
) : ViewModel() {
    private val _state =
        MutableStateFlow(AppStateInfoViewState(appVersion = configSource.appVersion))
    val state: StateFlow<AppStateInfoViewState> = _state.asStateFlow()

    init {
        if (sessionStore.user != null) {
            viewModelScope.launch {
                kotlin.runCatching {
                    contentSyncUseCase.execute()
                }
                _state.update { it.copy(isLanding = false) }
            }
        } else {
            _state.update { it.copy(isLanding = false) }
        }
        sessionStore.userFlow.onEach { user ->
            _state.update { it.copy(isOnboarded = user?.neighbourhoods?.isNotEmpty() ?: false) }
        }.launchIn(viewModelScope)
        configSource.isOnlineFlow.onEach { isOnline ->
            _state.update { it.copy(isOnline = isOnline.first, lastError = isOnline.second) }
        }.launchIn(viewModelScope)
        configSource.isAiOnlineFlow.onEach { isAiOnline ->
            _state.update { it.copy(isAiOnline = isAiOnline) }
        }.launchIn(viewModelScope)
        configSource.isTokenExpFlow.onEach { isTokenExp ->
            if(isTokenExp) {
                LogoutUseCase.execute(false)
            }
        }.launchIn(viewModelScope)
        configSource.wideScreenFlow.onEach { isWide ->
            _state.update { it.copy(isWideLand = isWide) }
        }.launchIn(viewModelScope)
        sessionStore.isLoggedInFlow.onEach { isLoggedIn ->
            _state.update { it.copy(isLoggedIn = isLoggedIn) }
        }.launchIn(viewModelScope)
    }

    data class AppStateInfoViewState(
        val appVersion: String = "",
        val isWideLand: Boolean = false,
        val isDebug: Boolean = true,
        val isOnline: Boolean = false,
        val isAiOnline: Boolean = false,
        val isLoggedIn: Boolean = false,
        val isOnboarded: Boolean = false,
        val isLanding: Boolean = true,
        val lastError: String? = null
    )
}