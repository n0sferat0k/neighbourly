package com.neighbourly.app.b_adapt.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.auth.RegisterUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import com.neighbourly.app.loadContentsFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    val registerUseCase: RegisterUseCase,
    val profileUpdateUseCase: ProfileImageUpdateUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterViewState())
    val state: StateFlow<RegisterViewState> = _state.asStateFlow()

    fun validateUsername(username: String) =
        _state.update { it.copy(usernameError = username.isBlank()) }

    fun validatePassword(
        password: String,
        confirmPassword: String,
    ) =
        _state.update { it.copy(passwordError = (password.isBlank() || password != confirmPassword)) }

    fun validateFullname(fullName: String) =
        _state.update { it.copy(fullnameError = fullName.isBlank()) }

    fun validateEmail(email: String) =
        _state.update { it.copy(emailError = (email.isBlank() || !email.isValidEmail())) }

    fun validatePhone(phoneNumber: String) =
        _state.update { it.copy(phoneError = (phoneNumber.isBlank() || !phoneNumber.isValidPhone())) }

    fun onRegister(
        username: String,
        password: String,
        confirmPassword: String,
        fullname: String,
        email: String,
        phone: String,
        profileImageFile: String?,
        remember: Boolean,
    ) {
        validateUsername(username)
        validatePassword(password, confirmPassword)
        validateFullname(fullname)
        validateEmail(email)
        validatePhone(phone)

        _state.value.let {
            if (it.usernameError || it.passwordError || it.fullnameError || it.emailError || it.phoneError) {
                return
            }
        }
        _state.update {
            it.copy(
                error = "",
                usernameError = false,
                passwordError = false,
                fullnameError = false,
                emailError = false,
                phoneError = false,
                loading = true,
            )
        }
        viewModelScope.launch {
            try {
                registerUseCase.execute(username, password, fullname, email, phone, remember)
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

    data class RegisterViewState(
        val usernameError: Boolean = false,
        val passwordError: Boolean = false,
        val fullnameError: Boolean = false,
        val emailError: Boolean = false,
        val phoneError: Boolean = false,
        val error: String = "",
        val loading: Boolean = false,
    )
}
