package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdMemberAddUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class HouseholdAddMemberViewModel(
    val sessionStore: SessionStore,
    val householdMemberAddUseCase: HouseholdMemberAddUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HouseholdAddMemberViewState())
    val state: StateFlow<HouseholdAddMemberViewState> = _state.asStateFlow()

    init {
        sessionStore.user
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            neighbourhoodsAndAcc =
                                user.neighbourhoods.map {
                                    NeighbourhoodAndAccVS(
                                        id = it.neighbourhoodid,
                                        name = it.name,
                                        access = it.access,
                                    )
                                },
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAddToHousehold() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", adding = true) }
                householdMemberAddUseCase.execute(_state.value.id, _state.value.username)
                _state.update { it.copy(error = "", adding = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, adding = false) }
            }
        }
    }

    fun updateNeighbourhoodAcc(
        id: Int,
        access: String,
    ) {
        _state.value.neighbourhoodsAndAcc
            .filter { it.id == id }
            .firstOrNull()
            ?.access
            ?.let { myAcc ->
                kotlin.runCatching {
                    val newAcc = max(access.toInt(), myAcc - 1)
                    val newAccs =
                        _state.value.neighbourhoodAndAccOverrideVS.apply { put(id, newAcc) }
                    _state.update { it.copy(neighbourhoodAndAccOverrideVS = newAccs) }
                }
            }
    }

    data class HouseholdAddMemberViewState(
        val id: Int = -1,
        val error: String = "",
        val adding: Boolean = false,
        val username: String = "",
        val fullname: String = "",
        val email: String = "",
        val phone: String = "",
        val about: String = "",
        val imageurl: String? = null,
        val neighbourhoodsAndAcc: List<NeighbourhoodAndAccVS> = emptyList(),
        val neighbourhoodAndAccOverrideVS: MutableMap<Int, Int> = mutableMapOf(),
    )

    data class NeighbourhoodAndAccVS(
        val id: Int,
        val name: String,
        val access: Int,
    )
}
