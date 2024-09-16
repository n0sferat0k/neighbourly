package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileFooterViewModel(
    val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileFooterViewState())
    val state: StateFlow<ProfileFooterViewState> = _state.asStateFlow()

    fun onLogout(logoutAll: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            logoutUseCase.execute(logoutAll)
            _state.update { it.copy(loading = false) }
        }
    }

    data class ProfileFooterViewState(
        val loading: Boolean = false,
    )
}
