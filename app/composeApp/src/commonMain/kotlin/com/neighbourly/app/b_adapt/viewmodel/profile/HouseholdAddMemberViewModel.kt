package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
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
import kotlin.math.min

class HouseholdAddMemberViewModel(
    val sessionStore: SessionStore,
    val householdMemberAddUseCase: HouseholdMemberAddUseCase,
    val fetchProfileUseCase: FetchProfileUseCase,
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
                                user.neighbourhoods
                                    .map {
                                        it.neighbourhoodid to
                                            NeighbourhoodAndAccVS(
                                                name = it.name,
                                                access = it.access,
                                            )
                                    }.toMap(),
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
        val neighbourhoodAndAcc = _state.value.neighbourhoodsAndAcc[id]!!
        val newAcc = min(access.toIntOrNull() ?: 0, neighbourhoodAndAcc.access - 1)
        _state.update {
            it.copy(
                neighbourhoodsAndAcc =
                    it.neighbourhoodsAndAcc
                        .toMutableMap()
                        .apply {
                            put(id, neighbourhoodAndAcc.copy(accessOverride = newAcc))
                        },
            )
        }
    }

    fun loadProfile(
        id: Int,
        username: String,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(error = "", loading = true) }
            try {
                fetchProfileUseCase.execute(id, username)?.let { user ->
                    _state.update {
                        it.copy(
                            error = "",
                            loading = false,
                            id = user.id,
                            username = user.username,
                            fullname = user.fullname,
                            email = user.email,
                            phone = user.phone,
                            about = user.about.orEmpty(),
                            imageurl = user.imageurl,
                        )
                    }
                } ?: run {
                    _state.update { it.copy(error = "User not found", loading = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    data class HouseholdAddMemberViewState(
        val loading: Boolean = false,
        val error: String = "",
        val id: Int = -1,
        val adding: Boolean = false,
        val username: String = "",
        val fullname: String = "",
        val email: String = "",
        val phone: String = "",
        val about: String = "",
        val imageurl: String? = null,
        val neighbourhoodsAndAcc: Map<Int, NeighbourhoodAndAccVS> = emptyMap(),
    )

    data class NeighbourhoodAndAccVS(
        val name: String,
        val access: Int,
        val accessOverride: Int? = null,
    )
}