package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdInfoUpdateUseCase
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HouseholdInfoEditViewModel(
    val householdInfoUpdateUseCase: HouseholdInfoUpdateUseCase,
    val householdImageUpdateUseCase: HouseholdImageUpdateUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(HouseholdInfoEditViewState())
    val state: StateFlow<HouseholdInfoEditViewState> = _state.asStateFlow()

    init {
        sessionStore.user
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            hasHousehold = true,
                            editableHousehold = household.headid == user.id,
                            imageurl = household.imageurl,
                            name = household.name,
                            address = household.address,
                            about = household.about,
                        )
                    }
                } ?: run {
                    _state.update {
                        HouseholdInfoEditViewState(
                            userQr =
                                user?.let {
                                    Json.encodeToString(
                                        UserQR(
                                            id = user.id,
                                            name = user.username,
                                            fullName = user.fullname,
                                            email = user.email,
                                            phone = user.phone,
                                        ),
                                    )
                                },
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun updateName(name: String) = _state.update { it.copy(nameOverride = name, nameError = name.isBlank()) }

    fun updateAddress(address: String) = _state.update { it.copy(addressOverride = address, addressError = address.isBlank()) }

    fun updateAbout(about: String) = _state.update { it.copy(aboutOverride = about) }

    fun onSaveHousehold() {
        _state.value.let {
            if (it.nameError || it.addressError) {
                return
            }

            viewModelScope.launch {
                try {
                    _state.update { it.copy(error = "", saving = true) }
                    householdInfoUpdateUseCase.execute(
                        it.nameOverride ?: it.name,
                        it.addressOverride ?: it.address,
                        it.aboutOverride ?: it.about,
                    )
                    _state.update { it.copy(error = "", saving = false) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, saving = false) }
                }
            }
        }
    }

    fun onHouseholdImageUpdate(fileContents: FileContents?) {
        viewModelScope.launch {
            try {
                fileContents?.let {
                    _state.update { it.copy(error = "", imageUpdating = true) }
                    householdImageUpdateUseCase.execute(it)
                    _state.update { it.copy(error = "", imageUpdating = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", imageUpdating = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, imageUpdating = false) }
            }
        }
    }

    data class HouseholdInfoEditViewState(
        val error: String = "",
        val saving: Boolean = false,
        val userQr: String? = null,
        val hasHousehold: Boolean = false,
        val editableHousehold: Boolean = false,
        val imageurl: String? = null,
        val imageUpdating: Boolean = false,
        val name: String = "",
        val address: String = "",
        val about: String = "",
        val nameOverride: String? = null,
        val addressOverride: String? = null,
        val aboutOverride: String? = null,
        val nameError: Boolean = false,
        val addressError: Boolean = false,
    )

    @Serializable
    data class UserQR(
        val id: Int,
        val name: String,
        val fullName: String,
        val phone: String,
        val email: String,
    )
}
