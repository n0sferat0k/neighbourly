package com.neighbourly.app.a_device.ui.atomic.organism.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.profile.ProfileImageItem
import com.neighbourly.app.a_device.ui.atomic.molecule.profile.ProfileMenuItem
import com.neighbourly.app.b_adapt.viewmodel.bean.OnboardVS
import com.neighbourly.app.d_entity.data.FileContents
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.home
import neighbourly.composeapp.generated.resources.map
import neighbourly.composeapp.generated.resources.polygon
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrganismProfileMenu(
    profile: OnboardVS,
    imageUpdating: Boolean = false,
    imageUpdateEnabled: Boolean = false,
    profileImageUpdate: (fileContents: FileContents?) -> Unit,
    profileImageSelect: () -> Unit,
    houseInfoSelect: () -> Unit,
    houseLocationSelect: () -> Unit,
    neighbourhoodInfoSelect: () -> Unit,
    highlightIndex: Int,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ProfileImageItem(
            imageurl = profile.imageurl,
            imageUpdateEnabled = imageUpdateEnabled,
            imageUpdating = imageUpdating,
            profileImageUpdate = profileImageUpdate,
            profileImageSelect = profileImageSelect,
        )

        Spacer(Modifier.weight(1f).fillMaxHeight())

        ProfileMenuItem(
            painter = painterResource(Res.drawable.home),
            error = !profile.hasHousehold,
            selected = highlightIndex == 1,
            onSelect = houseInfoSelect
        )

        Spacer(Modifier.width(14.dp).fillMaxHeight())

        ProfileMenuItem(
            painter = painterResource(Res.drawable.map),
            error = !profile.householdLocalized,
            selected = highlightIndex == 2,
            onSelect = houseLocationSelect
        )

        Spacer(Modifier.width(14.dp).fillMaxHeight())

        ProfileMenuItem(
            painter = painterResource(Res.drawable.polygon),
            error = !profile.hasNeighbourhoods,
            selected = highlightIndex == 3,
            onSelect = neighbourhoodInfoSelect
        )
    }
}
