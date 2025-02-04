package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toMemberVS
import com.neighbourly.app.c_business.usecase.profile.HouseholdManagementUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.loadContentsFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HouseholdInfoEditViewModel(
    val householdManagementUseCase: HouseholdManagementUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(HouseholdInfoEditViewState())
    val state: StateFlow<HouseholdInfoEditViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            household = HouseholdVS(
                                name = household.name,
                                address = household.address.orEmpty(),
                                about = household.about.orEmpty(),
                                imageurl = household.imageurl,
                                members = household.members?.map { it.toMemberVS() }.orEmpty(),
                            ),

                            isHouseHead = household.headid == user.id,
                        )
                    }
                } ?: run {
                    _state.update {
                        HouseholdInfoEditViewState(
                            userQr = user?.let { "${user.id},${user.username}" },
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onSaveHousehold(
        nameOverride: String? = null,
        addressOverride: String? = null,
        aboutOverride: String? = null,
    ) {
        _state.value.household.let {
            val nameError: Boolean = (nameOverride ?: it?.name)?.isBlank() ?: true
            val addressError: Boolean = (addressOverride ?: it?.address)?.isBlank() ?: true

            if (nameError || addressError) {
                return
            }

            viewModelScope.launch {
                try {
                    _state.update { it.copy(error = "", saving = true) }
                    householdManagementUseCase.updateInfo(
                        nameOverride ?: it?.name.orEmpty(),
                        addressOverride ?: it?.address.orEmpty(),
                        aboutOverride ?: it?.about.orEmpty(),
                    )
                    _state.update { it.copy(error = "", saving = false) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, saving = false) }
                }
            }
        }
    }

    fun onHouseholdImageUpdate(file: String) {
        viewModelScope.launch {
            try {
                loadContentsFromFile(file)?.let {
                    _state.update { it.copy(error = "", imageUpdating = true) }
                    householdManagementUseCase.updateImage(it)
                    _state.update { it.copy(error = "", imageUpdating = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", imageUpdating = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, imageUpdating = false) }
            }
        }
    }

    fun onLeaveHousehold() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "") }
                householdManagementUseCase.leaveHousehold()
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg) }
            }
        }
    }

    data class HouseholdInfoEditViewState(
        val household: HouseholdVS? = null,
        val isHouseHead: Boolean = false,

        val error: String = "",
        val saving: Boolean = false,
        val userQr: String? = null,

        val imageUpdating: Boolean = false,
    )
}
