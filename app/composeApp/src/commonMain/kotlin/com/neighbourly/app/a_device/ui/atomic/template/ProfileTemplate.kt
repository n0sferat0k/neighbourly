package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.molecule.card.LogoutCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdMemberScanner
import com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood.OrganismNeighbourhoodMemberScanner
import com.neighbourly.app.a_device.ui.atomic.organism.profile.person.OrganismPersonInfoEdit
import com.neighbourly.app.a_device.ui.atomic.organism.profile.OrganismProfileMenu
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.a_device.ui.atomic.page.HouseholdAddMemberPage
import com.neighbourly.app.a_device.ui.atomic.page.HouseholdInfoEditPage
import com.neighbourly.app.a_device.ui.atomic.page.NeighbourhoodAddMemberPage
import com.neighbourly.app.a_device.ui.atomic.page.HouseholdLocalizePage
import com.neighbourly.app.a_device.ui.atomic.page.NeighbourhoodInfoEditPage
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewState
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodAddMemberHousehold
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel
import com.neighbourly.app.d_entity.data.FileContents

@Composable
fun ProfileTemplate(
    state: ProfileViewModel.ProfileViewState,
    navigationState: NavigationViewState,
    refresh: () -> Unit,
    logout: (all: Boolean) -> Unit,
    profileImageUpdate: (fileContents: FileContents?) -> Unit,
    profileInfoSave: (fullnameOverride: String?, emailOverride: String?, phoneOverride: String?, aboutOverride: String?) -> Unit,
    householdMemberAdd: (id: Int, user: String) -> Unit,
    neighbourhoodMemberAdd: (neighbourhoodId: Int, id: Int, user: String) -> Unit,
    profileImageSelect: () -> Unit,
    houseInfoSelect: () -> Unit,
    houseLocationSelect: () -> Unit,
    neighbourhoodInfoSelect: () -> Unit,
) {
    OrganismContentBubble(
        scrollable = true,
        busy = state.loading,
        refresh = refresh,
        content = {
            OrganismProfileMenu(
                profile = state.onboardVS,
                imageUpdating = state.imageUpdating,
                imageUpdateEnabled = navigationState.profileContent == ProfileInfoEdit,
                highlightIndex = when (navigationState.profileContent) {
                    ProfileInfoEdit -> 0
                    is HouseholdAddMember,
                    HouseholdScanMember,
                    HouseholdInfoEdit -> 1
                    HouseholdLocalize -> 2
                    is NeighbourhoodAddMemberHousehold,
                    is NeighbourhoodScanMember,
                    NeighbourhoodInfoEdit -> 3
                },
                profileImageUpdate = profileImageUpdate,
                profileImageSelect = profileImageSelect,
                houseInfoSelect = houseInfoSelect,
                houseLocationSelect = houseLocationSelect,
                neighbourhoodInfoSelect = neighbourhoodInfoSelect,
            )

            Spacer(modifier = Modifier.height(8.dp))

            navigationState.profileContent.let {
                when (it) {
                    ProfileInfoEdit -> OrganismPersonInfoEdit(
                        profile = state.profile,
                        saving = state.saving,
                        onSave = profileInfoSave
                    )

                    HouseholdInfoEdit -> HouseholdInfoEditPage()
                    HouseholdLocalize -> HouseholdLocalizePage()
                    NeighbourhoodInfoEdit -> NeighbourhoodInfoEditPage()
                    is HouseholdAddMember -> HouseholdAddMemberPage(it.id, it.username)
                    HouseholdScanMember -> OrganismHouseholdMemberScanner(onScan = householdMemberAdd)
                    is NeighbourhoodScanMember -> OrganismNeighbourhoodMemberScanner(onScan = { id, user ->
                        neighbourhoodMemberAdd(it.neighbourhoodid, id, user)
                    })

                    is NeighbourhoodAddMemberHousehold -> NeighbourhoodAddMemberPage(
                        it.neighbourhoodid,
                        it.id,
                        it.username
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.error.isNotEmpty()) {
                FriendlyErrorText(state.error)
            }
        },
        footerContent = {
            LogoutCardFooter(logout)
        }
    )
}