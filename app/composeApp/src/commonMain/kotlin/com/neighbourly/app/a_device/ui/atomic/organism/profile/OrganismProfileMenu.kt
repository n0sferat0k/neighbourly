package com.neighbourly.app.a_device.ui.atomic.organism.profile

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
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.b_adapt.viewmodel.bean.OnboardVS
import com.neighbourly.app.d_entity.data.FileContents
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

    val defaultProfile = painterResource(Res.drawable.profile)
    var showFilePicker by remember { mutableStateOf(false) }

    FilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.platformFile?.toString()?.let {
            profileImageUpdate(loadContentsFromFile(it))
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        profile.imageurl.let {
            if (!it.isNullOrBlank() && !imageUpdating) {
                Box(
                    modifier =
                    Modifier
                        .size(80.dp)
                        .border(2.dp, AppColors.primary, CircleShape)
                        .clickable {
                            if (imageUpdateEnabled) {
                                showFilePicker = true
                            } else {
                                profileImageSelect()
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
                        if (imageUpdateEnabled) {
                            showFilePicker = true
                        } else {
                            profileImageSelect()
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
            if (profile.hasHousehold) {
                ColorFilter.tint(AppColors.primary)
            } else {
                ColorFilter.tint(AppColors.complementary)
            },
            modifier = Modifier
                .let {
                    if (highlightIndex == 1)
                        it.border(2.dp, AppColors.primary, CircleShape)
                    else it
                }
                .size(48.dp).clickable {
                    houseInfoSelect()
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.map),
            contentDescription = "HouseholdLocalize",
            contentScale = ContentScale.FillBounds,
            colorFilter =
            if (profile.householdLocalized) {
                ColorFilter.tint(AppColors.primary)
            } else {
                ColorFilter.tint(AppColors.complementary)
            },
            modifier = Modifier
                .let {
                    if (highlightIndex == 2)
                        it.border(2.dp, AppColors.primary, CircleShape)
                    else it
                }
                .size(48.dp).clickable {
                    houseLocationSelect()
                },
        )
        Spacer(Modifier.width(14.dp).fillMaxHeight())
        Image(
            painter = painterResource(Res.drawable.polygon),
            contentDescription = "Neighbourhood",
            contentScale = ContentScale.FillBounds,
            colorFilter =
            if (profile.hasNeighbourhoods) {
                ColorFilter.tint(AppColors.primary)
            } else {
                ColorFilter.tint(AppColors.complementary)
            },
            modifier = Modifier
                .let {
                    if (highlightIndex == 3)
                        it.border(2.dp, AppColors.primary, CircleShape)
                    else it
                }
                .size(48.dp).clickable {
                    neighbourhoodInfoSelect()
                },
        )
    }
}
