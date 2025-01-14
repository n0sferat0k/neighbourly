package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdAddMemberViewModel
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

@Composable
fun HouseholdAddMemberView(
    id: Int,
    username: String,
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: HouseholdAddMemberViewModel = viewModel { KoinProvider.KOIN.get<HouseholdAddMemberViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    val defaultProfile = painterResource(Res.drawable.profile)

    LaunchedEffect(id, username) {
        viewModel.loadProfile(id, username)
    }

    LaunchedEffect(state.added) {
        if (state.added) {
            navigationViewModel.goToHouseholdInfoEdit()
        }
    }

    Column {
        FriendlyText(text = stringResource(Res.string.attempting_to_add_household_member))

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            // Name Input
            OutlinedTextField(
                value = state.username,
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
                state.imageurl.let {
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

        Spacer(modifier = Modifier.height(8.dp))

        // Full Name Input
        OutlinedTextField(
            value = state.fullname,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.fullname)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input
        OutlinedTextField(
            value = state.email,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.email)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input
        OutlinedTextField(
            value = state.phone,
            onValueChange = { },
            enabled = false,
            label = { Text(stringResource(Res.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // About Input
        OutlinedTextField(
            value = state.about,
            onValueChange = { },
            enabled = false,
            maxLines = 5,
            label = { Text(stringResource(Res.string.about)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.neighbourhoodsAndAcc.size > 0) {
            FriendlyText(text = stringResource(Res.string.neighbourhood_acc))

            Spacer(modifier = Modifier.height(8.dp))

            state.neighbourhoodsAndAcc.toList().forEach { (id, item) ->
                OutlinedTextField(
                    value = (item.accessOverride ?: (item.access - 1)).toString(),
                    onValueChange = {
                        viewModel.updateNeighbourhoodAcc(id, it)
                    },
                    label = { Text(item.name) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        FriendlyButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(Res.string.add_to_household),
            loading = state.adding,
        ) {
            viewModel.onAddToHousehold()
        }

        if (state.error.isNotEmpty()) {
            FriendlyErrorText(state.error)
        }
    }
}
