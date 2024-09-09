package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.LogoutUseCase
import com.neighbourly.app.c_business.usecase.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.HttpImageLoader
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    val profileUpdateUseCase: ProfileImageUpdateUseCase,
    val logoutUseCase: LogoutUseCase,
    val profileRefreshUseCase: ProfileRefreshUseCase,
    val sessionStore: SessionStore,
    val httpImageLoader: HttpImageLoader,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRefreshUseCase.execute()
        }

        sessionStore.user
            .onEach { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            username = user.username,
                            fullName = user.fullname,
                            phone = user.phone,
                            email = user.email,
                        )
                    }
                }
                user?.imageurl?.let {
                    withContext(Dispatchers.IO) {
                        httpImageLoader.fetchImage(it).let { imageBytes ->
                            _state.update { it.copy(imageBytes = imageBytes) }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onProfileImageUpdate(fileContents: FileContents?) {
        viewModelScope.launch {
            try {
                fileContents?.let {
                    _state.update { it.copy(error = "", loading = true) }
                    profileUpdateUseCase.execute(it)
                    _state.update { it.copy(error = "", loading = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", loading = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    fun updateFullname(fullName: String) = _state.update { it.copy(fullName = fullName, fullnameError = fullName.isBlank()) }

    fun updateEmail(email: String) =
        _state.update {
            it.copy(
                email = email,
                emailError = (email.isBlank() || !email.isValidEmail()),
            )
        }

    fun updatePhone(phoneNumber: String) =
        _state.update {
            it.copy(
                phone = phoneNumber,
                phoneError = phoneNumber.let { it.isBlank() || !it.isValidPhone() },
            )
        }

    fun onLogout(logoutAll: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            logoutUseCase.execute(logoutAll)
            _state.update { it.copy(loading = false) }
        }
    }

    fun onSaveProfile() {
    }

    data class ProfileViewState(
        val error: String = "",
        val loading: Boolean = false,
        val username: String = "",
        val fullName: String = "",
        val email: String = "",
        val phone: String = "",
        val fullnameError: Boolean = false,
        val emailError: Boolean = false,
        val phoneError: Boolean = false,
        val imageBytes: ByteArray? = null,
    )
}
