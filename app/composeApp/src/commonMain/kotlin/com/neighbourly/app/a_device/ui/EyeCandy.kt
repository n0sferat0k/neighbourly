package com.neighbourly.app.a_device.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.confirm
import neighbourly.composeapp.generated.resources.curlzmt
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

data object AppColors {
    val primary: Color = Color(0xFF5BA9AE)
    val primaryLight: Color = Color(0xFFdeedee)
    val complementary: Color = Color(0xffae605b)
    val complementaryLight: Color = Color(0xFFeedfde)
}


@Composable
fun Alert(
    title: String,
    text: String,
    ok: (() -> Unit)? = null,
    cancel: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = { cancel?.invoke() },
        title = { CurlyText(text = title) },
        text = { CurlyText(text = text) },
        confirmButton = {
            CurlyButton(
                text = stringResource(Res.string.confirm),
                modifier = Modifier.padding(5.dp)
            ) {
                ok?.invoke()
            }
        },
        dismissButton = {
            CurlyButton(
                text = stringResource(Res.string.cancel),
                modifier = Modifier.padding(5.dp)
            ) {
                cancel?.invoke()
            }
        }
    )
}

@Composable
fun SwipeToDeleteBox(
    modifier: Modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
    onDelete: () -> Unit,
    content: @Composable (BoxScope.() -> Unit)
) {
    var offsetX by remember { mutableStateOf(0f) }
    Box(modifier = modifier) {
        // Background delete icon
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
        // Draggable content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
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
                .offset(x = offsetX.dp),
            content = content
        )
    }
}

@Composable
fun BoxHeader(
    modifier: Modifier = Modifier,
    busy: Boolean = false,
    refresh: (() -> Unit)? = null,
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
            text = stringResource(Res.string.app_name),
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
        Box(Modifier.fillMaxSize()) {
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

@Composable
fun font() =
    FontFamily(
        Font(
            Res.font.curlzmt,
            FontWeight.Normal,
            FontStyle.Normal,
        ),
    )

@Composable
fun CurlyText(
    modifier: Modifier = Modifier,
    text: String,
    bold: Boolean = false,
    fontSize: TextUnit = 20.sp,
) {
    Text(
        modifier = modifier,
        text = text,
        style =
        TextStyle(
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontFamily = font(),
            fontSize = fontSize,
            color = AppColors.primary,
        ),
    )
}

@Composable
fun CurlyButton(
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
        modifier
            .wrapContentWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier =
                Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = text,
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

@Composable
fun ErrorText(errMsg: String) {
    Text(
        text = errMsg,
        color = Color.Red,
        style =
        TextStyle(
            fontFamily = font(),
            fontSize = 18.sp,
        ),
    )
}

@Composable
fun HalfCircleHalo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawIntoCanvas { canvas ->
            drawRect(
                brush =
                Brush.radialGradient(
                    colors = listOf(Color.White, Color.White, Color.Transparent),
                    center = Offset(canvasWidth / 2, canvasHeight),
                    radius = canvasWidth / 2,
                ),
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, canvasHeight),
            )
        }
    }
}

@Composable
fun ContentBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
        modifier
            .alpha(.9f)
            .fillMaxSize()
            .padding(20.dp, 20.dp, 20.dp, 100.dp),
    ) {
        Box(
            modifier =
            Modifier
                .border(1.dp, AppColors.primary, RoundedCornerShape(20.dp))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                ),
            content = content,
        )
    }
}

@Composable
fun AutocompleteOutlinedTextField(
    modifier: Modifier = Modifier, label: @Composable (() -> Unit)? = null,
    entries: Map<Int, String>,
    onSelect: (Int) -> Unit,
) {
    var filterText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var filteredEntries by remember { mutableStateOf(entries.toList()) }

    LaunchedEffect(entries, filterText) {
        filteredEntries = entries.filter {
            it.value.lowercase().contains(filterText.lowercase())
        }.toList()
    }

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                expanded = false
            }
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                label = label,
                modifier = Modifier
                    .fillMaxWidth(),
                value = filterText,
                onValueChange = {
                    filterText = it
                    expanded = true
                },
                // Perform action when the TextField is clicked
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect { interaction ->
                                if (interaction is PressInteraction.Release) {
                                    expanded = !expanded
                                }
                            }
                        }
                    },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "arrow",
                        tint = Color.Black
                    )
                }
            )

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 160.dp),
                    ) {
                        items(filteredEntries) { (id, name) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        filterText = name
                                        expanded = false
                                        onSelect(id)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 15.dp)
                            ) {
                                Text(text = name, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}