package com.neighbourly.app.a_device.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.FriendlyText
import kotlinx.coroutines.delay

@Composable
fun MenuItemBox(
    modifier: Modifier,
    text: String,
    image: Painter,
    cornerShape: RoundedCornerShape,
    layoutDirection: LayoutDirection,
    delayMs: Long = 0,
    onClick: () -> Unit,
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMs)
        startAnimation = true
    }

    AnimatedVisibility(
        modifier = modifier.wrapContentSize(),
        visible = startAnimation,
        enter = when (layoutDirection) {
            LayoutDirection.Ltr -> slideInHorizontally(initialOffsetX = { -it })
            LayoutDirection.Rtl -> slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
        } + fadeIn()
    ) {
        Box(
            modifier = Modifier.alpha(.7f).height(48.dp)
                .clickable { onClick() }
                .border(1.dp, AppColors.primary, cornerShape).background(
                    color = Color.White,
                    shape = cornerShape,
                ),
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FriendlyText(
                        modifier = Modifier.padding(start = 10.dp)
                            .align(Alignment.CenterVertically),
                        text = text,
                        fontSize = 22.sp,
                    )
                    Image(
                        modifier = Modifier.size(48.dp).padding(end = 10.dp),
                        painter = image,
                        colorFilter = ColorFilter.tint(AppColors.primary),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun LeftMenuItemBox(
    modifier: Modifier,
    text: String,
    image: Painter,
    delayMs: Long = 0,
    onClick: () -> Unit,
) {
    MenuItemBox(
        modifier = modifier, text = text, image = image,
        cornerShape = RoundedCornerShape(
            topEnd = 20.dp,
            bottomEnd = 20.dp,
        ),
        layoutDirection = LayoutDirection.Ltr,
        delayMs = delayMs,
        onClick = onClick,
    )
}

@Composable
fun RightMenuItemBox(
    modifier: Modifier,
    text: String,
    image: Painter,
    delayMs: Long = 0,
    onClick: () -> Unit,
) {
    MenuItemBox(
        modifier = modifier, text = text, image = image,
        cornerShape = RoundedCornerShape(
            topStart = 20.dp,
            bottomStart = 20.dp,
        ),
        layoutDirection = LayoutDirection.Rtl,
        delayMs = delayMs,
        onClick = onClick,
    )
}
