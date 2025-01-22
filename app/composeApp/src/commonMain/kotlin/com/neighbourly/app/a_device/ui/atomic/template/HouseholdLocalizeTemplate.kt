package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdLocalizeComplete
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdLocalized
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdLocalizing
import com.neighbourly.app.a_device.ui.atomic.organism.profile.household.OrganismHouseholdUnLocalized
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.need_to_create_household
import org.jetbrains.compose.resources.stringResource

@Composable
fun HouseholdLocalizeTemplate(
    state: HouseholdLocalizeViewModel.HouseholdLocalizeViewState,
    onStartLocalize: () -> Unit,
    onStopLocalize: () -> Unit,
    onRelocate: () -> Unit,
    onAcceptLocalize: () -> Unit,
    onRetryLocalize: () -> Unit
) {
    when {
        state.localized -> OrganismHouseholdLocalized(
            canEditHousehold = state.canEditHousehold,
            onRelocate = onRelocate
        )

        state.hasHouse && !state.localized ->
            when {
                state.localizing && state.gpsprogress >= 1 ->
                    OrganismHouseholdLocalizeComplete(
                        onAccept = onAcceptLocalize,
                        onRetry = onRetryLocalize
                    )

                state.localizing && state.gpsprogress < 1 ->
                    OrganismHouseholdLocalizing(state.gpsprogress, onStop = onStopLocalize)

                else ->
                    OrganismHouseholdUnLocalized(
                        canEditHousehold = state.canEditHousehold,
                        onLocalize = onStartLocalize,
                    )
            }

        else -> FriendlyText(text = stringResource(Res.string.need_to_create_household))
    }
}
