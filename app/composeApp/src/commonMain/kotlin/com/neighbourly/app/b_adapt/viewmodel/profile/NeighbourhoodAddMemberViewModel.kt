package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.MemberVS
import com.neighbourly.app.b_adapt.viewmodel.bean.NameAndAccessVS
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NeighbourhoodAddMemberViewModel(
    val sessionStore: SessionStore,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
    val fetchProfileUseCase: FetchProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NeighbourhoodAddMemberViewState())
    val state: StateFlow<NeighbourhoodAddMemberViewState> = _state.asStateFlow()

    fun onAddToNeighbourhood(personsAndAcc: Map<Int, NameAndAccessVS>) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", adding = true) }
                neighbourhoodManagementUseCase.addMember(
                    neighbourhoodid = _state.value.neighbourhoodid,
                    id = _state.value.member.id,
                    username = _state.value.member.username,
                    accs = personsAndAcc.map { it.key to it.value.access }.toMap(),
                )
                _state.update { it.copy(error = "", adding = false, added = true) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, adding = false) }
            }
        }
    }

    fun loadProfile(
        neighbourhoodid: Int,
        id: Int,
        username: String,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    neighbourhoodid = neighbourhoodid,
                    myNeighbourhoodAcc = sessionStore.user?.neighbourhoods
                        ?.firstOrNull { it.neighbourhoodid == neighbourhoodid }
                        ?.access ?: 0,
                    error = "",
                    loading = true,
                    added = false,
                )
            }
            try {
                fetchProfileUseCase.execute(id, username)?.let { user ->
                    _state.update { state ->
                        state.copy(
                            member = MemberVS(
                                id = user.id,
                                username = user.username,
                                fullname = user.fullname.orEmpty(),
                                email = user.email.orEmpty(),
                                phone = user.phone.orEmpty(),
                                about = user.about.orEmpty(),
                                imageurl = user.imageurl,
                                hasEstablishedHousehold = user.household != null && user.household.location != null,
                            ),

                            personsAndAcc = user.household?.members?.map {
                                it.id to NameAndAccessVS(
                                    name = it.fullname.orEmpty(),
                                    access = _state.value.myNeighbourhoodAcc - 1,
                                )
                            }?.toMap() ?: emptyMap(),

                            error = "",
                            loading = false,
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

    data class NeighbourhoodAddMemberViewState(
        val neighbourhoodid: Int = 0,
        val myNeighbourhoodAcc: Int = 0,

        val member: MemberVS = MemberVS(),
        val personsAndAcc: Map<Int, NameAndAccessVS> = emptyMap(),

        val adding: Boolean = false,
        val added: Boolean = false,
        val loading: Boolean = false,
        val error: String = "",
    )
}
