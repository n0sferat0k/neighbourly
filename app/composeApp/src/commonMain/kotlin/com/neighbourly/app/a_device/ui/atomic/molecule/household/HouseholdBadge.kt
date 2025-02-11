package com.neighbourly.app.a_device.ui.atomic.molecule.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource

@Composable
fun HouseholdBadge(household: HouseholdVS, sizeMultiplyer: Int = 1, onClick: () -> Unit) {
    val defaultHouseImg = painterResource(Res.drawable.houses)

    Column(
        modifier = Modifier.widthIn(max = (72 * sizeMultiplyer).dp).clickable {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
            Modifier
                .size((36 * sizeMultiplyer).dp)
                .border((2 * sizeMultiplyer).dp, AppColors.primary, CircleShape),
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

        household.name.let {
            FriendlyText(
                text = it,
                fontSize = (12 * sizeMultiplyer).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}