package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.household_cannot_relocate
import neighbourly.composeapp.generated.resources.household_not_localized
import neighbourly.composeapp.generated.resources.need_to_localize
import neighbourly.composeapp.generated.resources.track_me
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismHouseholdUnLocalized(
    canEditHousehold: Boolean,
    onLocalize: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (canEditHousehold) {
            FriendlyText(text = stringResource(Res.string.need_to_localize))
            FriendlyButton(text = stringResource(Res.string.track_me)) {
                onLocalize()
            }
        } else {
            FriendlyText(text = stringResource(Res.string.household_not_localized))
            FriendlyText(text = stringResource(Res.string.household_cannot_relocate))
        }
    }
}