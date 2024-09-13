package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ProfileMenuViewModel(
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileMenuViewState())
    val state: StateFlow<ProfileMenuViewState> = _state.asStateFlow()

    init {
        sessionStore.user
            .filterNotNull()
            .onEach { user ->
                _state.update {
                    it.copy(
                        imageurl = user.imageurl,
                        hasHousehold = user.household != null,
                        householdLocalized = user.household?.location != null,
                        hasNeighbourhoods = user.neighbourhoods.isNotEmpty(),
                    )
                }
            }.launchIn(viewModelScope)
    }

    data class ProfileMenuViewState(
        val imageurl: String? = null,
        val hasHousehold: Boolean = false,
        val householdLocalized: Boolean = false,
        val hasNeighbourhoods: Boolean = false,
    )
}
