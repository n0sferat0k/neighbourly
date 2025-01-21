package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.MemberVS
import com.neighbourly.app.b_adapt.viewmodel.bean.NeighbourhoodAndAccVS
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdManagementUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HouseholdAddMemberViewModel(
    val sessionStore: SessionStore,
    val householdManagementUseCase: HouseholdManagementUseCase,
    val fetchProfileUseCase: FetchProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HouseholdAddMemberViewState())
    val state: StateFlow<HouseholdAddMemberViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            member = it.member.copy(
                                neighbourhoodsAndAcc = user.neighbourhoods
                                    .map {
                                        it.neighbourhoodid to
                                                NeighbourhoodAndAccVS(
                                                    name = it.name,
                                                    access = it.access - 1,
                                                )
                                    }.toMap(),
                            ),
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAddToHousehold(neighbourhoodsAndAcc: Map<Int, NeighbourhoodAndAccVS>) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", adding = true) }
                householdManagementUseCase.addMember(
                    id = _state.value.member.id,
                    username = _state.value.member.username,
                    access = neighbourhoodsAndAcc.map { it.key to it.value.access }.toMap()
                )
                _state.update { it.copy(error = "", adding = false, added = true) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, adding = false) }
            }
        }
    }

    fun loadProfile(
        id: Int,
        username: String,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(error = "", loading = true, added = false) }
            try {
                fetchProfileUseCase.execute(id, username)?.let { user ->
                    _state.update {
                        it.copy(
                            error = "",
                            loading = false,
                            member = it.member.copy(
                                id = user.id,
                                username = user.username,
                                fullname = user.fullname.orEmpty(),
                                email = user.email.orEmpty(),
                                phone = user.phone.orEmpty(),
                                about = user.about.orEmpty(),
                                imageurl = user.imageurl,
                            ),
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
        val member: MemberVS = MemberVS(),

        val loading: Boolean = false,
        val error: String = "",

        val adding: Boolean = false,
        val added: Boolean = false,
    )
}
