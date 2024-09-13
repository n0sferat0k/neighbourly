package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileMenuViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.home
import neighbourly.composeapp.generated.resources.map
import neighbourly.composeapp.generated.resources.polygon
import neighbourly.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileMenu(
    viewModel: ProfileMenuViewModel = viewModel { KoinProvider.KOIN.get<ProfileMenuViewModel>() },
    imageUpdating: Boolean = false,
    onContentSelected: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val defaultProfile = painterResource(Res.drawable.profile)

    Row(modifier = Modifier.fillMaxWidth()) {
        state.imageurl.let {
            if (!it.isNullOrBlank() && !imageUpdating) {
                Box(
                    modifier =
                        Modifier
                            .size(80.dp)
                            .border(2.dp, AppColors.primary, CircleShape)
                            .clickable {
                                onContentSelected(0)
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    KamelImage(
                        modifier = Modifier.size(80.dp).clip(CircleShape),
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
                }
            } else if (imageUpdating) {
                CircularProgressIndicator(color = AppColors.primary)
            } else {
                Image(
                    modifier =
                        Modifier.size(80.dp).clickable {
                            onContentSelected(0)
                        },
                    painter = defaultProfile,
                    contentDescription = "Profile Image",
                    colorFilter = ColorFilter.tint(AppColors.primary),
                )
            }
        }

        Spacer(Modifier.weight(1f).fillMaxHeight())

        Image(
            painter = painterResource(Res.drawable.home),
            contentDescription = "Home",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.hasHousehold) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    onContentSelected(1)
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.map),
            contentDescription = "Map",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.householdLocalized) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    onContentSelected(2)
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.polygon),
            contentDescription = "Map",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.hasNeighbourhoods) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    onContentSelected(3)
                },
        )
    }
}
