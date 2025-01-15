package com.neighbourly.app.a_device.ui.atomic.molecule.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.CurlyText
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CardHeader(
    modifier: Modifier = Modifier,
    busy: Boolean = false,
    title: String = stringResource(Res.string.app_name),
    refresh: (() -> Unit)? = null
) {
    Row(modifier = modifier.padding(start = 10.dp)) {
        Image(
            modifier = Modifier.size(48.dp).align(Alignment.CenterVertically),
            painter = painterResource(Res.drawable.houses),
            colorFilter = ColorFilter.tint(AppColors.primary),
            contentDescription = null,
        )
        CurlyText(
            modifier = Modifier.align(Alignment.Bottom).padding(start = 5.dp),
            text = title,
            fontSize = 24.sp,
        )

        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp).align(Alignment.Bottom).padding(start = 5.dp),
                color = AppColors.primary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (refresh != null) {
            Image(
                modifier =
                Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        refresh()
                    },
                painter = painterResource(Res.drawable.refresh),
                colorFilter = ColorFilter.tint(AppColors.primary),
                contentDescription = null,
            )
        }
    }
}