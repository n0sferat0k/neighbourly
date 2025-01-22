package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdEdit
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdMissing
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewState
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdInfoEditViewModel

@Composable
fun HouseholdInfoEditTemplate(
    state: HouseholdInfoEditViewModel.HouseholdInfoEditViewState,
    navigationState: NavigationViewState,
    onCreateHousehold: () -> Unit,
    onHouseholdImageUpdate: (file: String) -> Unit,
    onSaveHousehold: (name: String?, address: String?, about: String?) -> Unit,
    onAddMember: () -> Unit,
    onLeaveHousehold: () -> Unit,
) {
    if (state.household == null && !navigationState.addingNewHousehold) {
        OrganismHouseholdMissing(state.userQr, onCreateHousehold = onCreateHousehold)
    } else {
        OrganismHouseholdEdit(
            household = state.household,
            members = state.members,
            saving = state.saving,
            imageUpdating = state.imageUpdating,
            editableHousehold = state.isHouseHead || navigationState.addingNewHousehold,
            onImageUpdate = onHouseholdImageUpdate,
            onSaveHousehold = onSaveHousehold,
            onAddMember = onAddMember,
            onLeaveHousehold = onLeaveHousehold
        )
    }

    if (state.error.isNotEmpty()) {
        FriendlyErrorText(state.error)
    }
}
