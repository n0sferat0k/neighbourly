package com.neighbourly.app.a_device.ui.atomic.organism.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrganismHouseholdDetailsView(
    household: HouseholdVS,
    onHouseholdImage: () -> Unit
) {
    val defaultHouseImg = painterResource(Res.drawable.houses)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.wrapContentHeight().weight(1f)) {
                FriendlyText(text = household.name)
                if (household.members.isNotEmpty()) {
                    household.members.forEach {
                        FriendlyText(text = "* " + it, bold = true)
                    }
                }
            }

            Spacer(modifier = Modifier.width(3.dp))

            Box(
                modifier =
                Modifier
                    .size(60.dp)
                    .align(Alignment.CenterVertically)
                    .border(2.dp, AppColors.primary, CircleShape)
                    .clickable {
                        onHouseholdImage()
                    },
                contentAlignment = Alignment.Center,
            ) {
                household.imageurl.let {
                    if (!it.isNullOrBlank()) {
                        KamelImage(
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            resource = asyncPainterResource(data = it),
                            contentDescription = "Household Image",
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
                            painter = defaultHouseImg,
                            contentDescription = "Household Image",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                    }
                }
            }
        }

        FriendlyText(text = household.address)
        FriendlyText(text = household.about)


    }
}