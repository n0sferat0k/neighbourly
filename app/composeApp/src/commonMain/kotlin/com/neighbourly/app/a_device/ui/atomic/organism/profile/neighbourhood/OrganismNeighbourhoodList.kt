package com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.NeighbourhoodVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.addperson
import neighbourly.composeapp.generated.resources.confirm_leaving_neighbourhood
import neighbourly.composeapp.generated.resources.exit
import neighbourly.composeapp.generated.resources.leaving
import neighbourly.composeapp.generated.resources.list_neighbourhoods
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismNeighbourhoodList(
    neighbourhoods: List<NeighbourhoodVS>,
    canLeave: Boolean,
    onAddMember: (neighbourhoodId: Int) -> Unit,
    onLeave: (neighbourhoodId: Int) -> Unit,
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (showRemoveAlertForId > -1) {
        OrganismAlertDialog(
            title = stringResource(Res.string.leaving) + " " + neighbourhoods.firstOrNull { it.id == showRemoveAlertForId }?.name,
            text = stringResource(Res.string.confirm_leaving_neighbourhood),
            ok = {
                onLeave(showRemoveAlertForId)
                showRemoveAlertForId = -1
            },
            cancel = {
                showRemoveAlertForId = -1
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        FriendlyText(text = stringResource(Res.string.list_neighbourhoods))

        neighbourhoods.toList().forEach { neighbourhood ->
            FriendlyText(
                modifier = Modifier.align(Alignment.Start),
                text = "${neighbourhood.name} [Acc: ${neighbourhood.acc}]",
                fontSize = 24.sp,
                bold = true
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(Res.drawable.addperson),
                    contentDescription = "Add to neighbourhood",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(AppColors.primary),
                    modifier =
                    Modifier.size(48.dp).clickable {
                        onAddMember(neighbourhood.id)
                    },
                )

                if (canLeave) {
                    Spacer(modifier = Modifier.width(20.dp))

                    Image(
                        painter = painterResource(Res.drawable.exit),
                        contentDescription = "Leave neighbourhood",
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(AppColors.primary),
                        modifier =
                        Modifier.size(48.dp).clickable {
                            showRemoveAlertForId = neighbourhood.id
                        },
                    )
                }
            }
        }
    }
}