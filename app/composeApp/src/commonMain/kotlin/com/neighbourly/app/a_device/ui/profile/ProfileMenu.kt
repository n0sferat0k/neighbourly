package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.ProfileContent
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileMenuViewModel
import com.neighbourly.app.loadContentsFromFile
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
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: ProfileMenuViewModel = viewModel { KoinProvider.KOIN.get<ProfileMenuViewModel>() },
    imageUpdating: Boolean = false,
) {
    val navigation by navigationViewModel.state.collectAsState()
    val state by viewModel.state.collectAsState()
    val defaultProfile = painterResource(Res.drawable.profile)

    var showFilePicker by remember { mutableStateOf(false) }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.platformFile?.toString()?.let {
            viewModel.onProfileImageUpdate(loadContentsFromFile(it))
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        state.imageurl.let {
            if (!it.isNullOrBlank() && !imageUpdating) {
                Box(
                    modifier =
                        Modifier
                            .size(80.dp)
                            .border(2.dp, AppColors.primary, CircleShape)
                            .clickable {
                                if (navigation.profileContent == ProfileContent.ProfileInfoEdit) {
                                    showFilePicker = true
                                } else {
                                    navigationViewModel.goToProfileInfoEdit()
                                }
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
                            if (navigation.profileContent == ProfileContent.ProfileInfoEdit) {
                                showFilePicker = true
                            } else {
                                navigationViewModel.goToProfileInfoEdit()
                            }
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
            contentDescription = "Household",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.hasHousehold) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    navigationViewModel.goToHouseholdInfoEdit()
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.map),
            contentDescription = "HouseholdLocalize",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.householdLocalized) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    navigationViewModel.goToHouseholdLocalize()
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.polygon),
            contentDescription = "Neighbourhood",
            contentScale = ContentScale.FillBounds,
            colorFilter =
                if (state.hasNeighbourhoods) {
                    ColorFilter.tint(AppColors.primary)
                } else {
                    ColorFilter.tint(AppColors.complementary)
                },
            modifier =
                Modifier.size(48.dp).clickable {
                    navigationViewModel.goToNeighbourhoodInfoEdit()
                },
        )
    }
    if (state.error.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        ErrorText(state.error)
    }
}
