package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.localize_progress
import neighbourly.composeapp.generated.resources.stop
import org.jetbrains.compose.resources.stringResource
import java.math.RoundingMode

@Composable
fun OrganismHouseholdLocalizing(progress: Float, onStop: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FriendlyText(text = stringResource(Res.string.localize_progress))

        Box(modifier = Modifier.size(150.dp)) {
            FriendlyText(
                modifier = Modifier.align(Alignment.Center),
                text =
                (minOf(1f, progress) * 100)
                    .toBigDecimal()
                    .setScale(1, RoundingMode.UP)
                    .toString() + " %",
                fontSize = 40.sp,
                bold = true,
            )
            if (progress < 0.1) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.primary,
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = progress,
                    color = AppColors.primary,
                )
            }
        }

        FriendlyButton(text = stringResource(Res.string.stop)) {
            onStop()
        }
    }
}