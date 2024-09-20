package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NeighbourhoodInfoViewModel(
    val sessionStore: SessionStore,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NeighbourhoodInfoViewState())
    val state: StateFlow<NeighbourhoodInfoViewState> = _state.asStateFlow()

    init {
        sessionStore.localization
            .onEach { localization ->
                _state.update {
                    it.copy(
                        drawing = localization.drawing,
                        drawingDone = !localization.drawingPoints.isNullOrEmpty(),
                    )
                }
            }.launchIn(viewModelScope)

        sessionStore.user
            .onEach { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            hasLocalizedHouse = user.household != null && user.household.location != null,
                            hasNeighbourhoods = user.neighbourhoods.isNotEmpty(),
                            neighbourhoods = user.neighbourhoods.map { it.name },
                            userQr =
                                Json.encodeToString(
                                    UserQR(
                                        id = user.id,
                                        name = user.username,
                                        fullName = user.fullname,
                                        email = user.email,
                                        phone = user.phone,
                                    ),
                                ),
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

    fun updateName(name: String) = _state.update { it.copy(nameOverride = name, nameError = name.isBlank()) }

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

    data class NeighbourhoodInfoViewState(
        val error: String = "",
        val saving: Boolean = false,
        val drawing: Boolean = false,
        val drawingDone: Boolean = false,
        val hasLocalizedHouse: Boolean = false,
        val hasNeighbourhoods: Boolean = false,
        val neighbourhoods: List<String> = emptyList(),
        val userQr: String? = null,
        val name: String = "",
        val nameOverride: String? = null,
        val nameError: Boolean = false,
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
