package com.neighbourly.app.b_adapt.viewmodel.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodAddMemberHousehold
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

public class NavigationViewModel(
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(NavigationViewState())
    val state: StateFlow<NavigationViewState> = _state.asStateFlow()

    init {
        sessionStore.user
            .onEach { user ->
                val loggedIn = user != null
                if (loggedIn != _state.value.userLoggedIn) {
                    _state.update {
                        it.copy(
                            userLoggedIn = loggedIn,
                            mainContentVisible = true,
                            addingNewHousehold = false,
                            profileContent = ProfileInfoEdit,
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setDisableMainContentToggle(disableMainToggle: Boolean) {
        _state.update {
            it.copy(
                disableMainToggle = disableMainToggle,
                mainContentVisible = if (it.disableMainToggle) true else it.mainContentVisible,
            )
        }
    }

    fun toggleMainContent() {
        _state.update { it.copy(mainContentVisible = if (it.disableMainToggle) true else !it.mainContentVisible) }
    }

    fun goToScanMemberForHousehold() {
        _state.update { it.copy(mainContentVisible = true, profileContent = HouseholdScanMember) }
    }

    fun goToScanMemberHouseholdForNeighbourhood(neighbourhoodid: Int) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                profileContent = NeighbourhoodScanMember(neighbourhoodid),
            )
        }
    }

    fun goToProfileInfoEdit() {
        _state.update { it.copy(mainContentVisible = true, profileContent = ProfileInfoEdit) }
    }

    fun goToHouseholdInfoEdit() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                profileContent = HouseholdInfoEdit,
                addingNewHousehold = false,
            )
        }
    }

    fun goToHouseholdLocalize() {
        _state.update { it.copy(mainContentVisible = true, profileContent = HouseholdLocalize) }
    }

    fun goToNeighbourhoodInfoEdit() {
        _state.update { it.copy(mainContentVisible = true, profileContent = NeighbourhoodInfoEdit) }
    }

    fun goToNeighbourhoodAddHousehold(
        neighbourhoodid: Int,
        id: Int,
        username: String,
    ) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                profileContent =
                    NeighbourhoodAddMemberHousehold(
                        neighbourhoodid,
                        id,
                        username,
                    ),
            )
        }
    }

    fun goToHouseholdAddMember(
        id: Int,
        username: String,
    ) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                profileContent = HouseholdAddMember(id, username),
            )
        }
    }

    fun goToAddHousehold() {
        _state.update {
            it.copy(addingNewHousehold = true)
        }
    }

    fun goToMap() {
        _state.update {
            it.copy(mainContentVisible = if (it.disableMainToggle) true else false)
        }
    }

    data class NavigationViewState(
        val userLoggedIn: Boolean = false,
        val disableMainToggle: Boolean = false,
        val mainContentVisible: Boolean = false,
        val addingNewHousehold: Boolean = false,
        val profileContent: ProfileContent = ProfileInfoEdit,
    )

    sealed class ProfileContent {
        object ProfileInfoEdit : ProfileContent()

        object HouseholdScanMember : ProfileContent()

        data class NeighbourhoodScanMember(
            val neighbourhoodid: Int,
        ) : ProfileContent()

        data class HouseholdAddMember(
            val id: Int,
            val username: String,
        ) : ProfileContent()

        data class NeighbourhoodAddMemberHousehold(
            val neighbourhoodid: Int,
            val id: Int,
            val username: String,
        ) : ProfileContent()

        object HouseholdInfoEdit : ProfileContent()

        object HouseholdLocalize : ProfileContent()

        object NeighbourhoodInfoEdit : ProfileContent()
    }
}
