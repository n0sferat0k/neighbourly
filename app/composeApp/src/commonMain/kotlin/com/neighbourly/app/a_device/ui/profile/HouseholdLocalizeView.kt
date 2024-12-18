package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.CurlyButton
import com.neighbourly.app.a_device.ui.utils.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.accept
import neighbourly.composeapp.generated.resources.household_cannot_relocate
import neighbourly.composeapp.generated.resources.household_localized
import neighbourly.composeapp.generated.resources.household_not_localized
import neighbourly.composeapp.generated.resources.household_relocate
import neighbourly.composeapp.generated.resources.localize_completed
import neighbourly.composeapp.generated.resources.localize_progress
import neighbourly.composeapp.generated.resources.need_to_create_household
import neighbourly.composeapp.generated.resources.need_to_localize
import neighbourly.composeapp.generated.resources.relocate
import neighbourly.composeapp.generated.resources.retry
import neighbourly.composeapp.generated.resources.track_me
import org.jetbrains.compose.resources.stringResource
import java.math.RoundingMode

@Composable
fun HouseholdLocalizeView(viewModel: HouseholdLocalizeViewModel = viewModel { KoinProvider.KOIN.get<HouseholdLocalizeViewModel>() }) {
    val state by viewModel.state.collectAsState()

    if (!state.hasHouse) {
        FriendlyText(text = stringResource(Res.string.need_to_create_household))
    } else {
        if (state.localized) {
            FriendlyText(text = stringResource(Res.string.household_localized))
            if (state.editableHousehold) {
                FriendlyText(text = stringResource(Res.string.household_relocate))
                Spacer(modifier = Modifier.height(16.dp))
                CurlyButton(text = stringResource(Res.string.relocate)) {
                    viewModel.onRelocate()
                }
            } else {
                FriendlyText(text = stringResource(Res.string.household_cannot_relocate))
            }
        } else {
            if (state.editableHousehold) {
                if (state.localizing) {
                    if (state.gpsprogress >= 1) {
                        FriendlyText(text = stringResource(Res.string.localize_completed))

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            CurlyButton(text = stringResource(Res.string.accept)) {
                                viewModel.onAccept()
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            CurlyButton(text = stringResource(Res.string.retry)) {
                                viewModel.onRetry()
                            }
                        }
                    } else {
                        FriendlyText(text = stringResource(Res.string.localize_progress))

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.size(150.dp)) {
                            FriendlyText(
                                modifier = Modifier.align(Alignment.Center),
                                text =
                                    (minOf(1f, state.gpsprogress) * 100)
                                        .toBigDecimal()
                                        .setScale(1, RoundingMode.UP)
                                        .toString() + " %",
                                fontSize = 40.sp,
                                bold = true,
                            )
                            if (state.gpsprogress < 0.1) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    color = AppColors.primary,
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    progress = state.gpsprogress,
                                    color = AppColors.primary,
                                )
                            }
                        }
                    }
                } else {
                    FriendlyText(text = stringResource(Res.string.need_to_localize))

                    Spacer(modifier = Modifier.height(16.dp))

                    CurlyButton(text = stringResource(Res.string.track_me)) {
                        viewModel.onLocalize()
                    }
                }
            } else {
                FriendlyText(text = stringResource(Res.string.household_not_localized))
                FriendlyText(text = stringResource(Res.string.household_cannot_relocate))
            }
        }
    }
}
