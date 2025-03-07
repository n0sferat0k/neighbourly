package com.neighbourly.app.b_adapt.viewmodel.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.AiInterface
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BackendInfo
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.HouseholdDetails
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ItemDetails
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavPages.HideMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavPages.ShowMenuAndReset
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodAddMemberHousehold
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebGallery
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebPage
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Stack

public class NavigationViewModel(
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(NavigationViewState())
    private val _stateStack = Stack<NavigationViewState>()
    val state: StateFlow<NavigationViewState> = _state.asStateFlow()

    init {
        sessionStore.isLoggedInFlow.onEach {
            when (it) {
                true -> _state.update {
                    it.copy(mainContentVisible = true)
                }

                false -> _state.update {
                    NavigationViewState(disableMainToggle = _state.value.disableMainToggle)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun disableMainToggle(disable: Boolean) {
        _state.update {
            it.copy(
                disableMainToggle = disable
            )
        }
    }

    fun toggleMainContent() {
        if (_state.value.disableMainToggle || !_state.value.mainContentVisible) {
            _state.updateAndClearStack(ShowMenuAndReset.updater)
        } else {
            _state.update(HideMenu.updater)
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

    fun goToFindItems(
        itemType: ItemTypeVS? = null,
        householdId: Int? = null,
        itemIds: List<Int>? = null
    ) {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = MainContent.FindItems(itemType, householdId, itemIds),
            )
        }
    }

    fun goToBackendInfo() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = BackendInfo,
            )
        }
    }

    fun goToAiPage() {
        _state.update {
            it.copy(
                mainContentVisible = true,
                mainContent = AiInterface,
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
        val updater: (NavigationViewState) -> NavigationViewState = {
            it.copy(
                mainContentVisible = if (it.disableMainToggle) true else false,
                webContent = WebMap,
            )
        }

        if (_state.value.disableMainToggle) {
            _state.update(updater)
        } else {
            _state.updateAndStack(updater)
        }
    }

    fun goHome() =
        _state.updateAndClearStack {
            it.copy(
                mainContentVisible = if (it.disableMainToggle) true else false,
                webContent = WebMap,
            )
        }

    fun goToHouseholdDetails(householdId: Int) {
        _state.updateAndStack {
            it.copy(
                mainContentVisible = true,
                mainContent = HouseholdDetails(householdId)
            )
        }
    }

    fun goToItemDetails(itemId: Int) {
        _state.updateAndStack {
            it.copy(
                mainContentVisible = true,
                mainContent = ItemDetails(itemId)
            )
        }
    }

    fun goToHouseholdGallery(householdId: Int) {
        val updater: (NavigationViewState) -> NavigationViewState = {
            it.copy(
                mainContentVisible = if (it.disableMainToggle) true else false,
                webContent = WebGallery(householdId = householdId, imageId = householdId)
            )
        }
        if (_state.value.disableMainToggle) {
            _state.update(updater)
        } else {
            _state.updateAndStack(updater)
        }
    }

    fun goToItemGallery(itemId: Int, imageId: Int) {
        val updater: (NavigationViewState) -> NavigationViewState = {
            it.copy(
                mainContentVisible = if (it.disableMainToggle) true else false,
                webContent = WebGallery(itemId = itemId, imageId = imageId)
            )
        }
        if (_state.value.disableMainToggle) {
            _state.update(updater)
        } else {
            _state.updateAndStack(updater)
        }
    }

    fun goToWebPage(url: String) {
        val updater: (NavigationViewState) -> NavigationViewState = {
            it.copy(
                mainContentVisible = if (it.disableMainToggle) true else false,
                webContent = WebPage(url)
            )
        }
        if (_state.value.disableMainToggle) {
            _state.update(updater)
        } else {
            _state.updateAndStack(updater)
        }
    }

    fun goBack(): Boolean =
        _state.popStack()

    private fun MutableStateFlow<NavigationViewState>.updateAndClearStack(updater: (NavigationViewState) -> NavigationViewState) =
        this.update {
            _stateStack.clear()
            updater(it)
        }

    private fun MutableStateFlow<NavigationViewState>.updateAndStack(updater: (NavigationViewState) -> NavigationViewState) =
        this.update {
            _stateStack.push(it)
            updater(it)
        }

    private fun MutableStateFlow<NavigationViewState>.popStack() =
        if (_stateStack.empty()) {
            goHome()
            true
        } else {
            _stateStack.pop().let { previousState ->
                this.update {
                    previousState
                }
                true
            }
        }

}
