package com.neighbourly.app.b_adapt.viewmodel.navigation

import androidx.lifecycle.ViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent.ProfileInfoEdit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NavigationViewState())
    val state: StateFlow<NavigationViewState> = _state.asStateFlow()

    fun setDisableMainContentToggle(disableMainToggle: Boolean) {
        _state.update { it.copy(disableMainToggle = disableMainToggle) }
    }

    fun toggleMainContent() {
        _state.update { it.copy(mainContentVisible = if (it.disableMainToggle) true else !it.mainContentVisible) }
    }

    fun goToAddMember() {
        _state.update { it.copy(mainContentVisible = true, profileContent = HouseholdScanMember) }
    }

    fun goToProfileInfoEdit() {
        _state.update { it.copy(mainContentVisible = true, profileContent = ProfileInfoEdit) }
    }

    fun goToHouseholdInfoEdit() {
        _state.update { it.copy(mainContentVisible = true, profileContent = HouseholdInfoEdit) }
    }

    fun goToHouseholdLocalize() {
        _state.update { it.copy(mainContentVisible = true, profileContent = HouseholdLocalize) }
    }

    fun goToNeighbourhoodInfoEdit() {
        _state.update { it.copy(mainContentVisible = true, profileContent = NeighbourhoodInfoEdit) }
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

    data class NavigationViewState(
        val disableMainToggle: Boolean = false,
        val mainContentVisible: Boolean = false,
        val profileContent: ProfileContent = ProfileInfoEdit,
    )

    sealed class ProfileContent {
        object ProfileInfoEdit : ProfileContent()

        object HouseholdScanMember : ProfileContent()

        data class HouseholdAddMember(
            val id: Int,
            val username: String,
        ) : ProfileContent()

        object HouseholdInfoEdit : ProfileContent()

        object HouseholdLocalize : ProfileContent()

        object NeighbourhoodInfoEdit : ProfileContent()
    }
}
