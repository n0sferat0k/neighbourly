package com.neighbourly.app.b_adapt.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.LoginUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    val loginUseCase: LoginUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginViewState())
    val state: StateFlow<LoginViewState> = _state.asStateFlow()

    fun refresh() {
        _state.update {
            it.copy(
                username = sessionStore.credentials?.username.orEmpty(),
                password = sessionStore.credentials?.password.orEmpty()
            )
        }
    }

    fun onLogin(
        remember: Boolean,
    ) {
        _state.update { it.copy(error = "", loading = true) }
        viewModelScope.launch {
            try {
                loginUseCase.execute(_state.value.username, _state.value.password, remember)
                _state.update { it.copy(error = "", loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    fun updateUsername(user: String) {
        _state.update { it.copy(username = user) }
    }

    fun updatePassword(pass: String) {
        _state.update { it.copy(password = pass) }
    }

    data class LoginViewState(
        val error: String = "",
        val loading: Boolean = false,
        val username: String = "",
        val password: String = ""
    )
}
