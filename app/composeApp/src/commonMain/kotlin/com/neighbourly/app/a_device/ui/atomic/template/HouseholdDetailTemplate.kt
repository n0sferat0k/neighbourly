package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.card.OkCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.household.OrganismHouseholdDetailsView
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismItemList
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.household.HouseholdDetailsViewModel

@Composable
fun HouseholdDetailTemplate(
    state: HouseholdDetailsViewModel.HouseholdDetailsViewState,
    onHouseholdImage: () -> Unit,
    onMuteHouse: (muted: Boolean) -> Unit,
    onMuteHouseMember: (id: Int, muted: Boolean) -> Unit,
    onSelectItem: (id: Int) -> Unit,
    onClose: () -> Unit,
) {
    OrganismContentBubble(
        scrollable = false,
        content = {
            state.household?.let { household ->
                OrganismHouseholdDetailsView(
                    household = household,
                    onHouseholdImage = onHouseholdImage,
                    onMuteHouse = onMuteHouse,
                    onMuteHouseMember = onMuteHouseMember,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OrganismItemList(
                state.items,
                onSelectItem = onSelectItem,
                onDeleteItem = {},
                onSelectHousehold = {})
        },
        footerContent = {
            OkCardFooter(onClose)
        }
    )
}