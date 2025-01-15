package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.NeighbourhoodVS
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NeighbourhoodInfoViewModel(
    val sessionStore: SessionStore,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NeighbourhoodInfoViewState())
    val state: StateFlow<NeighbourhoodInfoViewState> = _state.asStateFlow()

    init {
        sessionStore.localizationFlow
            .onEach { localization ->
                _state.update {
                    it.copy(
                        drawing = localization.drawing,
                        drawingDone = !localization.drawingPoints.isNullOrEmpty(),
                    )
                }
            }.launchIn(viewModelScope)

        sessionStore.userFlow
            .onEach { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            hasLocalizedHouse = user.household != null && user.household.location != null,
                            hasNeighbourhoods = user.neighbourhoods.isNotEmpty(),
                            isHouseholdHead = user.household?.headid == user.id,
                            userQr = "${user.id},${user.username}",
                            neighbourhoods = user.neighbourhoods.map {
                                NeighbourhoodVS(
                                    id = it.neighbourhoodid,
                                    name = it.name,
                                    acc = it.access,
                                )
                            },
                        )
                    }
                } ?: run {
                    _state.update { NeighbourhoodInfoViewState() }
                }
            }.launchIn(viewModelScope)
    }

    fun createNeighbourhood() {
        viewModelScope.launch {
            neighbourhoodManagementUseCase.startDrawing()
        }
    }

    fun onCancelNeighbourhoodCreate() {
        viewModelScope.launch {
            neighbourhoodManagementUseCase.cancelDrawing()
        }
    }

    fun updateName(name: String) =
        _state.update { it.copy(nameOverride = name, nameError = name.isBlank()) }

    fun onSaveNeighbourhood() {
        _state.value.let {
            if (it.nameError) {
                return
            }

            viewModelScope.launch {
                try {
                    _state.update { it.copy(error = "", saving = true) }
                    neighbourhoodManagementUseCase.saveNeighbourhood(
                        name = it.nameOverride ?: it.name,
                    )
                    _state.update { it.copy(error = "", saving = false) }
                } catch (e: OpException) {
                    _state.update { it.copy(error = e.msg, saving = false) }
                }
            }
        }
    }

    fun leaveNeighbourhood(neighbourhoodId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "") }
                neighbourhoodManagementUseCase.leaveNeighbourhood(neighbourhoodId)
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg) }
            }
        }
    }


    data class NeighbourhoodInfoViewState(
        val error: String = "",
        val saving: Boolean = false,
        val drawing: Boolean = false,
        val drawingDone: Boolean = false,
        val hasLocalizedHouse: Boolean = false,
        val hasNeighbourhoods: Boolean = false,
        val isHouseholdHead: Boolean = false,
        val neighbourhoods: List<NeighbourhoodVS> = emptyList(),
        val userQr: String? = null,
        val name: String = "",
        val nameOverride: String? = null,
        val nameError: Boolean = false,
    )

}
