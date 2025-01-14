package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.painterResource
import kotlin.math.absoluteValue

@Composable
fun SwipeToDeleteContainer(
    modifier: Modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
    onDelete: (() -> Unit)? = null,
    content: @Composable (BoxScope.() -> Unit)
) {
    var offsetX by remember { mutableStateOf(0f) }
    Box(modifier = modifier) {
        // Background delete icon
        if (onDelete != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.delete),
                    contentDescription = "Delete",
                    tint = AppColors.complementary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        // Draggable content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    if (onDelete != null) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX.absoluteValue > 100) {
                                    onDelete()
                                }
                                offsetX = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                offsetX += dragAmount.toDp().value
                                change.consume()
                            }
                        )
                    }
                }
                .offset(x = offsetX.dp),
            content = content
        )
    }
}