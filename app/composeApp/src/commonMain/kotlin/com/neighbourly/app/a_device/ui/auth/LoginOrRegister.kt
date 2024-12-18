package com.neighbourly.app.a_device.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import com.neighbourly.app.a_device.ui.utils.curlyFont
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.register
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginOrRegister() {
    var index by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start))

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            when (index) {
                0 -> Login()
                1 -> Register()
            }
        }

        Row(modifier = Modifier.align(Alignment.End).padding(bottom = 10.dp, end = 20.dp)) {
            Text(
                modifier =
                    Modifier
                        .padding(start = 5.dp)
                        .clickable(onClick = {
                            index = 0
                        }),
                text = stringResource(Res.string.login),
                style =
                    TextStyle(
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = curlyFont(),
                        fontSize = 20.sp,
                        color = AppColors.primary,
                    ),
            )
            Text(
                modifier =
                    Modifier
                        .padding(start = 15.dp)
                        .clickable(onClick = {
                            index = 1
                        }),
                text = stringResource(Res.string.register),
                style =
                    TextStyle(
                        fontWeight = if (index == 1) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = curlyFont(),
                        fontSize = 20.sp,
                        color = AppColors.primary,
                    ),
            )
        }
    }
}
