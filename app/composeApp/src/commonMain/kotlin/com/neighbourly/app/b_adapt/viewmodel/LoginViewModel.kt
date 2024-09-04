package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.LoginUseCase
import com.neighbourly.app.d_entity.data.OpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginViewState())
    val state: StateFlow<LoginViewState> = _state.asStateFlow()

    fun onLogin(
        username: String,
        password: String,
    ) {
        _state.update { it.copy(error = "", loading = true) }
        viewModelScope.launch {
            try {
                loginUseCase.execute(username, password)
                _state.update { it.copy(error = "", loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    data class LoginViewState(
        val error: String = "",
        val loading: Boolean = false,
    )
}
