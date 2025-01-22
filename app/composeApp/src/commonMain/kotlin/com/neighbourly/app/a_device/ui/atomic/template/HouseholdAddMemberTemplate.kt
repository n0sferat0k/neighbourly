package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdMemberAdd
import com.neighbourly.app.b_adapt.viewmodel.bean.NameAndAccessVS
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdAddMemberViewModel

@Composable
fun HouseholdAddMemberTemplate(
    state: HouseholdAddMemberViewModel.HouseholdAddMemberViewState,
    onAddToHousehold: (neighbourhoodsAndAcc: Map<Int, NameAndAccessVS>) -> Unit,
) {

    OrganismHouseholdMemberAdd(
        member = state.member,
        neighbourhoodsAndAcc = state.neighbourhoodsAndAcc,
        adding = state.adding,
        onAddToHousehold = onAddToHousehold
    )

    if (state.error.isNotEmpty()) {
        FriendlyErrorText(state.error)
    }
}
