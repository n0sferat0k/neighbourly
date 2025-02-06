package com.neighbourly.app.b_adapt.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.LoginUseCase
import com.neighbourly.app.c_business.usecase.auth.RegisterUseCase
import com.neighbourly.app.c_business.usecase.auth.ResetUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import com.neighbourly.app.loadContentsFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginRegisterViewModel(
    val resetUseCase: ResetUseCase,
    val sessionStore: SessionStore,
    val loginUseCase: LoginUseCase,
    val registerUseCase: RegisterUseCase,
    val profileUpdateUseCase: ProfileImageUpdateUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginRegisterViewState())
    val state: StateFlow<LoginRegisterViewState> = _state.asStateFlow()

    fun refresh() {
        _state.update {
            it.copy(
                rememberedUsername = sessionStore.credentials?.username.orEmpty(),
                rememberedPassword = sessionStore.credentials?.password.orEmpty()
            )
        }
    }

    fun onLogin(
        username: String,
        password: String,
        remember: Boolean,
    ) {
        if(_state.value.loading) return

        _state.update { it.copy(error = "", loading = true) }
        viewModelScope.launch {
            try {
                loginUseCase.execute(username, password, remember)
                _state.update { it.copy(error = "", loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    fun onRegister(
        username: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        email: String,
        phoneNumber: String,
        profileImageFile: String?,
        remember: Boolean,
    ) {
        if(_state.value.loading) return

        val usernameError = username.isBlank()
        val passwordError = (password.isBlank() || password != confirmPassword)
        val fullnameError = fullName.isBlank()
        val emailError = !email.isValidEmail()
        val phoneError = !phoneNumber.isValidPhone()

        _state.value.let {
            if (usernameError || passwordError || fullnameError || emailError || phoneError) {
                return
            }
        }
        _state.update {
            it.copy(
                error = "",
                loading = true,
            )
        }
        viewModelScope.launch {
            try {
                registerUseCase.execute(username, password, fullName, email, phoneNumber, remember)
                profileImageFile?.let {
                    loadContentsFromFile(it)?.let {
                        profileUpdateUseCase.execute(it)
                    }
                }
                _state.update { it.copy(error = "", loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    fun onReset(email: String?) {
        if(_state.value.loading) return

        if(email == null || !email.isValidEmail()) {
            _state.update { it.copy(error = "", loading = false, resetComplete = false) }
        } else {
            _state.update { it.copy(error = "", loading = true) }
            viewModelScope.launch {
                try {
                    resetUseCase.execute(email)
                    _state.update { it.copy(error = "", loading = false, resetComplete = true) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, loading = false) }
                }
            }
        }
    }

    data class LoginRegisterViewState(
        val error: String = "",
        val loading: Boolean = false,
        val resetComplete: Boolean = false,
        val rememberedUsername: String = "",
        val rememberedPassword: String = ""
    )
}
