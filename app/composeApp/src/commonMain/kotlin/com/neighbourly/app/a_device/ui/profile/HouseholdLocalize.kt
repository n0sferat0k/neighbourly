package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.localize_progress
import neighbourly.composeapp.generated.resources.need_to_localize
import neighbourly.composeapp.generated.resources.track_me
import org.jetbrains.compose.resources.stringResource

@Composable
fun HouseholdLocalize(viewModel: HouseholdLocalizeViewModel = viewModel { KoinProvider.KOIN.get<HouseholdLocalizeViewModel>() }) {
    val state by viewModel.state.collectAsState()

    if (!state.localized) {
        if (state.localizing) {
            CurlyText(text = stringResource(Res.string.localize_progress))

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.size(150.dp)) {
                CurlyText(
                    modifier = Modifier.align(Alignment.Center),
                    text =
                        (state.gpsprogress * 100)
                            .toBigDecimal()
                            .setScale(1)
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
        } else {
            CurlyText(text = stringResource(Res.string.need_to_localize))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.onLocalize()
                },
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
            ) {
                Text(
                    stringResource(Res.string.track_me),
                    color = Color.White,
                    style =
                        TextStyle(
                            fontFamily = font(),
                            fontSize = 18.sp,
                            color = AppColors.primary,
                        ),
                )
            }
        }
    }
}
