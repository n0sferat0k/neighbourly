package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileRefreshUseCase: ProfileRefreshUseCase,
    val logoutUseCase: LogoutUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    fun refresh() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            runCatching {
                profileRefreshUseCase.execute()
            }
            _state.update { it.copy(loading = false) }
        }
    }


    fun onLogout(logoutAll: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            logoutUseCase.execute(logoutAll)
            _state.update { it.copy(loading = false) }
        }
    }

    data class ProfileViewState(
        val loading: Boolean = false,
    )
}
