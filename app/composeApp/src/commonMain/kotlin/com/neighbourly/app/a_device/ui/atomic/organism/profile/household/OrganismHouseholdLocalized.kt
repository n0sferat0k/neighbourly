package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.household_cannot_relocate
import neighbourly.composeapp.generated.resources.household_localized
import neighbourly.composeapp.generated.resources.household_relocate
import neighbourly.composeapp.generated.resources.relocate
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismHouseholdLocalized(
    canEditHousehold: Boolean,
    onRelocate: () -> Unit
) {
    FriendlyText(text = stringResource(Res.string.household_localized))

    if (canEditHousehold) {
        FriendlyText(text = stringResource(Res.string.household_relocate))
        Spacer(modifier = Modifier.height(16.dp))
        FriendlyButton(text = stringResource(Res.string.relocate)) {
            onRelocate()
        }
    } else {
        FriendlyText(text = stringResource(Res.string.household_cannot_relocate))
    }
}