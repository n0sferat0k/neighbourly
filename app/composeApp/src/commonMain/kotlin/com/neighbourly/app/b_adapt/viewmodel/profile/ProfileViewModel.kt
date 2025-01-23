package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.OnboardVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ProfileVS
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileUpdateUseCase
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
    val profileRefreshUseCase: ProfileRefreshUseCase,
    val profileImageUpdateUseCase: ProfileImageUpdateUseCase,
    val profileUpdateUseCase: ProfileUpdateUseCase,
    val householdLocalizeUseCase: HouseholdLocalizeUseCase,
    val logoutUseCase: LogoutUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach {
                it?.let { user ->
                    _state.update {
                        it.copy(
                            onboardVS = OnboardVS(
                                imageurl = user.imageurl,
                                hasHousehold = user.household != null,
                                householdLocalized = user.household?.location != null,
                                hasNeighbourhoods = user.neighbourhoods.isNotEmpty(),
                            ),
                            profile = ProfileVS(
                                username = user.username,
                                fullname = user.fullname.orEmpty(),
                                email = user.email.orEmpty(),
                                phone = user.phone.orEmpty(),
                                about = user.about.orEmpty(),
                            )
                        )
                    }
                } ?: run {
                    _state.update { it.copy(onboardVS = OnboardVS(), profile = ProfileVS()) }
                }
            }.launchIn(viewModelScope)
    }

    fun refresh() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            runCatching {
                kotlin.runCatching {
                    profileRefreshUseCase.execute()
                    if (sessionStore.user?.localizing == true) {
                        householdLocalizeUseCase.fetchGpsLogs()
                        householdLocalizeUseCase.fetchGpsCandidate()
                    }
                }
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

    fun onProfileImageUpdate(fileContents: FileContents?) {
        viewModelScope.launch {
            try {
                fileContents?.let {
                    _state.update { it.copy(error = "", imageUpdating = true) }
                    profileImageUpdateUseCase.execute(it)
                    _state.update { it.copy(error = "", imageUpdating = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", imageUpdating = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, imageUpdating = false) }
            }
        }
    }

    fun onSaveProfile(
        fullnameOverride: String? = null,
        emailOverride: String? = null,
        phoneOverride: String? = null,
        aboutOverride: String? = null
    ) {
        _state.value.profile.let {
            val fullnameError = (fullnameOverride ?: it.fullname).isBlank()
            val emailError = (emailOverride ?: it.email).let { it.isBlank() || !it.isValidEmail() }
            val phoneError = (phoneOverride ?: it.phone).let { it.isBlank() || !it.isValidPhone() }

            if (fullnameError || emailError || phoneError) {
                return
            }

            viewModelScope.launch {
                try {
                    _state.update { it.copy(error = "", saving = true) }
                    profileUpdateUseCase.execute(
                        fullname = fullnameOverride ?: it.fullname,
                        email = emailOverride ?: it.email,
                        phone = phoneOverride ?: it.phone,
                        about = aboutOverride ?: it.about,
                    )
                    _state.update { it.copy(error = "", saving = false) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, saving = false) }
                }
            }
        }
    }

    data class ProfileViewState(
        val loading: Boolean = false,
        val saving: Boolean = false,
        val imageUpdating: Boolean = false,
        val onboardVS: OnboardVS = OnboardVS(),
        val profile: ProfileVS = ProfileVS(),
        val error: String = ""
    )
}
