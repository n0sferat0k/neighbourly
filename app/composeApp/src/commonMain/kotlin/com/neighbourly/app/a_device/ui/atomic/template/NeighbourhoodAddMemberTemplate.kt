package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood.OrganismNeighbourhoodMemberAdd
import com.neighbourly.app.b_adapt.viewmodel.bean.NameAndAccessVS
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodAddMemberViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.cannot_add_to_neighbourhood_member
import org.jetbrains.compose.resources.stringResource

@Composable
fun NeighbourhoodAddMemberTemplate(
    state: NeighbourhoodAddMemberViewModel.NeighbourhoodAddMemberViewState,
    onAddToNeighbourhood: (personsAndAcc: Map<Int, NameAndAccessVS>) -> Unit
) {
    if (!state.member.hasEstablishedHousehold) {
        FriendlyText(text = stringResource(Res.string.cannot_add_to_neighbourhood_member))
    } else {
        OrganismNeighbourhoodMemberAdd(
            member = state.member,
            adding = state.adding,
            personsAndAcc = state.personsAndAcc,
            onAddToNeighbourhood = onAddToNeighbourhood
        )
    }

    if (state.error.isNotEmpty()) {
        FriendlyErrorText(state.error)
    }
}
