package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.LogoutUseCase
import com.neighbourly.app.c_business.usecase.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.ProfileRefreshUseCase
import com.neighbourly.app.c_business.usecase.ProfileUpdateUseCase
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileImageUpdateUseCase: ProfileImageUpdateUseCase,
    val profileUpdateUseCase: ProfileUpdateUseCase,
    val logoutUseCase: LogoutUseCase,
    val profileRefreshUseCase: ProfileRefreshUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching {
                profileRefreshUseCase.execute()
            }
            _state.update { it.copy(loading = false) }
        }

        sessionStore.user
            .onEach { user ->
                println("AAAAAAAAAAAAAAA got user update with imageUrl: " + user?.imageurl)
                user?.let {
                    _state.update {
                        it.copy(
                            user =
                                UserVS(
                                    username = user.username,
                                    fullname = user.fullname,
                                    phone = user.phone,
                                    email = user.email,
                                    imageurl = user.imageurl,
                                    about = user.about.orEmpty(),
                                    household =
                                        user.household?.let {
                                            HouseholdVS(
                                                name = it.name,
                                                address = it.address,
                                                isLocalized = it.location != null,
                                            )
                                        },
                                    neighbourhoods =
                                        user.neighbourhoods.map {
                                            NeighbourhoodVS(
                                                name = it.name,
                                            )
                                        },
                                ),
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onProfileImageUpdate(fileContents: FileContents?) {
        viewModelScope.launch {
            try {
                fileContents?.let {
                    _state.update { it.copy(error = "", imageUpdating = true) }
                    profileImageUpdateUseCase.execute(it)
                    _state.update { it.copy(error = "", imageUpdating = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", saving = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
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

    fun onLogout(logoutAll: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            logoutUseCase.execute(logoutAll)
            _state.update { it.copy(loading = false) }
        }
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
                        it.fullnameOverride ?: it.user.fullname,
                        it.emailOverride ?: it.user.email,
                        it.phoneOverride ?: it.user.phone,
                        it.aboutOverride ?: it.user.about,
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
        val loading: Boolean = false,
        val imageUpdating: Boolean = false,
        val saving: Boolean = false,
        val fullnameOverride: String? = null,
        val emailOverride: String? = null,
        val phoneOverride: String? = null,
        val aboutOverride: String? = null,
        val fullnameError: Boolean = false,
        val emailError: Boolean = false,
        val phoneError: Boolean = false,
        val user: UserVS = UserVS(),
    )

    data class UserVS(
        val username: String = "",
        val fullname: String = "",
        val email: String = "",
        val phone: String = "",
        val about: String = "",
        val imageurl: String? = null,
        val household: HouseholdVS? = null,
        val neighbourhoods: List<NeighbourhoodVS> = emptyList(),
    )

    data class HouseholdVS(
        val name: String,
        val address: String,
        val isLocalized: Boolean,
    )

    data class NeighbourhoodVS(
        val name: String,
    )
}
