package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.bean.MemberVS
import com.neighbourly.app.b_adapt.viewmodel.bean.NameAndAccessVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.add_to_household
import neighbourly.composeapp.generated.resources.attempting_to_add_household_member
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.neighbourhood_acc
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
fun OrganismHouseholdMemberAdd(
    member: MemberVS,
    neighbourhoodsAndAcc: Map<Int, NameAndAccessVS>,
    adding: Boolean,
    onAddToHousehold: (neighbourhoodsAndAcc: Map<Int, NameAndAccessVS>) -> Unit,
) {
    val defaultProfile = painterResource(Res.drawable.profile)

    var neighbourhoodsAndAccOverride by remember {
        mutableStateOf(
            neighbourhoodsAndAcc
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FriendlyText(text = stringResource(Res.string.attempting_to_add_household_member))

        Row {
            // Name Input
            OutlinedTextField(
                value = member.username,
                onValueChange = { },
                enabled = false,
                label = { Text(stringResource(Res.string.username)) },
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(3.dp))

            Box(
                modifier =
                Modifier
                    .size(60.dp)
                    .align(Alignment.Bottom)
                    .border(2.dp, AppColors.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                member.imageurl.let {
                    if (!it.isNullOrBlank()) {
                        KamelImage(
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            resource = asyncPainterResource(data = it),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            onLoading = { progress ->
                                CircularProgressIndicator(
                                    progress = progress,
                                    color = AppColors.primary,
                                )
                            },
                        )
                    } else {
                        Image(
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            painter = defaultProfile,
                            contentDescription = "Household Image",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                    }
                }
            }
        }

        // Full Name Input
        OutlinedTextField(
            value = member.fullname,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.fullname)) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Email Input
        OutlinedTextField(
            value = member.email,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.email)) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Phone Number Input
        OutlinedTextField(
            value = member.phone,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
        )

        // About Input
        OutlinedTextField(
            value = member.about,
            onValueChange = { },
            enabled = false,
            maxLines = 5,
            label = { Text(stringResource(Res.string.about)) },
            modifier = Modifier.fillMaxWidth(),
        )

        if (neighbourhoodsAndAccOverride.isNotEmpty()) {

            FriendlyText(text = stringResource(Res.string.neighbourhood_acc))

            neighbourhoodsAndAccOverride.toList().forEach { (id, item) ->
                OutlinedTextField(
                    value = item.access.toString(),
                    onValueChange = { access ->
                        neighbourhoodsAndAcc[id]?.let { neighbourhoodAndAcc ->
                            neighbourhoodsAndAccOverride = neighbourhoodsAndAccOverride
                                .toMutableMap()
                                .apply {
                                    put(
                                        id, neighbourhoodAndAcc.copy(
                                            access = min(
                                                access.toIntOrNull() ?: 0,
                                                neighbourhoodsAndAcc[id]?.access ?: 0
                                            )
                                        )
                                    )
                                }
                        }
                    },
                    label = { Text(item.name) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        FriendlyButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(Res.string.add_to_household),
            loading = adding,
        ) {
            onAddToHousehold(neighbourhoodsAndAccOverride)
        }
    }
}