package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood.OrganismNeighbourhoodJoinOrCreate
import com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood.OrganismNeighbourhoodList
import com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood.OrganismNeighbourhoodNew
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodInfoViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.need_to_have_household
import neighbourly.composeapp.generated.resources.no_neighbourhood
import org.jetbrains.compose.resources.stringResource

@Composable
fun NeighbourhoodInfoEditTemplate(
    state: NeighbourhoodInfoViewModel.NeighbourhoodInfoViewState,
    onAddMember: (neighbourhoodId: Int) -> Unit,
    onLeave: (neighbourhoodId: Int) -> Unit,
    onCreateNew: () -> Unit,
    onSaveNew: (name: String) -> Unit,
    onCancelNew: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            !state.hasLocalizedHouse ->
                FriendlyText(text = stringResource(Res.string.need_to_have_household))

            state.geofenceDrawing ->
                OrganismNeighbourhoodNew(
                    hasGeofence = state.geofenceDrawn,
                    saving = state.saving,
                    onSave = onSaveNew,
                    onCancel = onCancelNew
                )

            else -> {
                if (state.hasNeighbourhoods) {
                    OrganismNeighbourhoodList(
                        neighbourhoods = state.neighbourhoods,
                        canLeave = state.isHouseholdHead,
                        onLeave = onLeave,
                        onAddMember = onAddMember
                    )
                } else {
                    FriendlyText(text = stringResource(Res.string.no_neighbourhood))
                }

                OrganismNeighbourhoodJoinOrCreate(
                    userQr = state.userQr,
                    canCreate = state.isHouseholdHead,
                    onCreate = onCreateNew
                )
            }
        }
    }
}
