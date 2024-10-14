package com.neighbourly.app.b_adapt.viewmodel.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodAddMemberHousehold
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.d_entity.data.ItemType
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
        sessionStore.isLoggedIn.onEach {
            when (it) {
                true -> _state.update {
                    it.copy(userLoggedIn = true, mainContentVisible = true)
                }

                false -> _state.update {
                    NavigationViewState(
                        disableMainToggle = _state.value.disableMainToggle
                    )
                }
            }
        }.launchIn(viewModelScope)

        sessionStore.user
            .onEach { user ->
                user?.let {
                    _state.update {
                        it.copy(restrictedContent = user.household?.location == null || user.neighbourhoods.isEmpty())
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
        if (_state.value.mainContentVisible) {
            _state.update {
                it.copy(
                    mainContentVisible = it.disableMainToggle,
                    mainContent = MainMenu,
                    profileContent = ProfileInfoEdit,
                    addingNewHousehold = false,
                )
            }
        } else {
            _state.update { it.copy(mainContentVisible = true) }
        }
    }

    fun goToProfile() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = ProfileInfoEdit,
            )
        }
    }

    fun goToMainPage(page: MainContent) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = page,
            )
        }
    }

    fun goToFindItems(itemType: ItemType? =  null, householdId: Int? = null) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = MainContent.FindItems(itemType, householdId),
            )
        }
    }

    fun goToScanMemberForHousehold() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = HouseholdScanMember,
            )
        }
    }

    fun goToScanMemberHouseholdForNeighbourhood(neighbourhoodid: Int) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = NeighbourhoodScanMember(neighbourhoodid),
            )
        }
    }

    fun goToProfileInfoEdit() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = ProfileInfoEdit,
            )
        }
    }

    fun goToHouseholdInfoEdit() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = HouseholdInfoEdit,
                addingNewHousehold = false,
            )
        }
    }

    fun goToHouseholdLocalize() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = HouseholdLocalize,
            )
        }
    }

    fun goToNeighbourhoodInfoEdit() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = NeighbourhoodInfoEdit,
            )
        }
    }

    fun goToNeighbourhoodAddHousehold(
        neighbourhoodid: Int,
        id: Int,
        username: String,
    ) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
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
                mainContent = ManageProfile,
                profileContent = HouseholdAddMember(id, username),
            )
        }
    }

    fun goToAddHousehold() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = ManageProfile,
                profileContent = HouseholdInfoEdit,
                addingNewHousehold = true,
            )
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
        val restrictedContent: Boolean = true,
        val mainContentVisible: Boolean = true,
        val addingNewHousehold: Boolean = false,
        val mainContent: MainContent = MainMenu,
        val profileContent: ProfileContent = ProfileInfoEdit,
    )

    sealed interface MainContent {
        object MainMenu : MainContent

        object ManageProfile : MainContent
        object ManageMyStuff : MainContent
        object PublishStuff : MainContent

        data class FindItems(
            val type: ItemType? = null,
            val householdId: Int? = null
        ) : MainContent
    }

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
