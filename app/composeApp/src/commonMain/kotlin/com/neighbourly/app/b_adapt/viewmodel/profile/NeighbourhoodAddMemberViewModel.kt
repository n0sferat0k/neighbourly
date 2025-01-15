package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.PersonAndAccVS
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
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
import kotlin.math.min

class NeighbourhoodAddMemberViewModel(
    val sessionStore: SessionStore,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
    val fetchProfileUseCase: FetchProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NeighbourhoodAddMemberViewState())
    val state: StateFlow<NeighbourhoodAddMemberViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            neighbourhoods =
                                user.neighbourhoods
                                    .map { it.neighbourhoodid to it.access }
                                    .toMap(),
                            personsAndAcc =
                                it.personsAndAcc?.let {
                                    val myAcc =
                                        user.neighbourhoods
                                            .filter { it.neighbourhoodid == _state.value.neighbourhoodid }
                                            .firstOrNull()
                                            ?.access ?: 0

                                    it
                                        .map { (id, personAndAcc) ->
                                            id to
                                                personAndAcc.copy(
                                                    access = myAcc - 1,
                                                    accessOverride =
                                                        min(
                                                            personAndAcc.accessOverride ?: 0,
                                                            myAcc - 1,
                                                        ),
                                                )
                                        }.toMap()
                                },
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAddToNeighbourhood() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", adding = true) }
                neighbourhoodManagementUseCase.addMember(
                    _state.value.neighbourhoodid,
                    _state.value.id,
                    _state.value.username,
                    _state.value.personsAndAcc
                        ?.map { (id, personAndAcc) ->
                            id to (personAndAcc.accessOverride ?: personAndAcc.access)
                        }?.toMap(),
                )
                _state.update { it.copy(error = "", adding = false, added = true) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, adding = false) }
            }
        }
    }

    fun updatePersonAcc(
        personid: Int,
        access: String,
    ) {
        val personAndAcc = _state.value.personsAndAcc?.get(personid)!!
        val newAcc = min(access.toIntOrNull() ?: 0, personAndAcc.access)
        _state.update {
            it.copy(
                personsAndAcc =
                    it.personsAndAcc
                        ?.toMutableMap()
                        ?.apply {
                            put(personid, personAndAcc.copy(accessOverride = newAcc))
                        },
            )
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
                    error = "",
                    loading = true,
                    added = false,
                )
            }
            try {
                fetchProfileUseCase.execute(id, username)?.let { user ->
                    _state.update { state ->
                        state.copy(
                            error = "",
                            loading = false,
                            hasEstablishedHousehold = user.household != null && user.household.location != null,
                            id = user.id,
                            username = user.username,
                            fullname = user.fullname.orEmpty(),
                            email = user.email.orEmpty(),
                            phone = user.phone.orEmpty(),
                            about = user.about.orEmpty(),
                            imageurl = user.imageurl,
                            personsAndAcc =
                                user.household?.members?.let { members ->
                                    val myAcc = state.neighbourhoods?.get(state.neighbourhoodid) ?: 0

                                    members
                                        .map {
                                            it.id to
                                                PersonAndAccVS(
                                                    name = it.fullname.orEmpty(),
                                                    access = myAcc - 1,
                                                )
                                        }.toMap()
                                },
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
        val loading: Boolean = false,
        val error: String = "",
        val hasEstablishedHousehold: Boolean = false,
        val id: Int = -1,
        val adding: Boolean = false,
        val added: Boolean = false,
        val username: String = "",
        val fullname: String = "",
        val email: String = "",
        val phone: String = "",
        val about: String = "",
        val imageurl: String? = null,
        val neighbourhoods: Map<Int, Int>? = null,
        val personsAndAcc: Map<Int, PersonAndAccVS>? = null,
    )

}
