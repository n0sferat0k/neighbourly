package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.ProfileUpdateUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileInfoEditViewModel(
    val profileUpdateUseCase: ProfileUpdateUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    init {
        sessionStore.user
            .filterNotNull()
            .onEach { user ->
                _state.update {
                    it.copy(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        phone = user.phone,
                        about = user.about.orEmpty(),
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun updateFullname(fullName: String) = _state.update { it.copy(fullnameOverride = fullName, fullnameError = fullName.isBlank()) }

    fun updateEmail(email: String) =
        _state.update {
            it.copy(
                emailOverride = email,
                emailError = (email.isBlank() || !email.isValidEmail()),
            )
        }

    fun updatePhone(phoneNumber: String) =
        _state.update {
            it.copy(
                phoneOverride = phoneNumber,
                phoneError = phoneNumber.let { it.isBlank() || !it.isValidPhone() },
            )
        }

    fun updateAbout(about: String) =
        _state.update {
            it.copy(
                aboutOverride = about,
            )
        }

    fun onSaveProfile() {
        _state.value.let {
            if (it.fullnameError || it.emailError || it.phoneError) {
                return
            }

            viewModelScope.launch {
                try {
                    _state.update { it.copy(error = "", saving = true) }
                    profileUpdateUseCase.execute(
                        it.fullnameOverride ?: it.fullname,
                        it.emailOverride ?: it.email,
                        it.phoneOverride ?: it.phone,
                        it.aboutOverride ?: it.about,
                    )
                    _state.update { it.copy(error = "", saving = false) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, saving = false) }
                }
            }
        }
    }

    data class ProfileViewState(
        val error: String = "",
        val saving: Boolean = false,
        val username: String = "",
        val fullname: String = "",
        val email: String = "",
        val phone: String = "",
        val about: String = "",
        val fullnameOverride: String? = null,
        val emailOverride: String? = null,
        val phoneOverride: String? = null,
        val aboutOverride: String? = null,
        val fullnameError: Boolean = false,
        val emailError: Boolean = false,
        val phoneError: Boolean = false,
    )
}
