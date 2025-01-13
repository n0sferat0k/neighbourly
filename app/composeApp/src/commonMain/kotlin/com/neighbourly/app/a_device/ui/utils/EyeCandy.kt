package com.neighbourly.app.a_device.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.CurlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

data object AppColors {
    val primary: Color = Color(0xFF5BA9AE)
    val primaryLight: Color = Color(0xFFdeedee)
    val complementary: Color = Color(0xffae605b)
    val complementaryLight: Color = Color(0xFFeedfde)
    val error: Color = Color(0xFFff0000)
}

@Composable
fun BoxHeader(
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

@Composable
fun BoxScrollableContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.wrapContentSize().padding(20.dp).align(Alignment.TopCenter),
                content = content,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops =
                        arrayOf(
                            0.0f to Color.White,
                            0.05f to Color.Transparent,
                            0.95f to Color.Transparent,
                            1f to Color.White,
                        ),
                    ),
                ),
        )
    }
}

@Composable
fun BoxStaticContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth()) {
        Box(Modifier.wrapContentSize()) {
            Box(
                modifier = Modifier.wrapContentSize().padding(20.dp).align(Alignment.TopCenter),
                content = content,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops =
                        arrayOf(
                            0.0f to Color.White,
                            0.05f to Color.Transparent,
                            0.95f to Color.Transparent,
                            1f to Color.White,
                        ),
                    ),
                ),
        )
    }
}

@Composable
fun BoxFooter(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.padding(bottom = 10.dp, end = 20.dp),
        content = content,
    )
}

