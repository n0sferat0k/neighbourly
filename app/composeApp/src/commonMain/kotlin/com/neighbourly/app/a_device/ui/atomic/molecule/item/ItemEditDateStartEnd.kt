package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.datetime.OrganismDateTimeDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_end
import neighbourly.composeapp.generated.resources.add_start
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.end_date
import neighbourly.composeapp.generated.resources.start_date
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@Composable
fun ItemEditDateStartEnd(
    hidden: Boolean = false,
    start: Instant?,
    end: Instant?,
    onStartChange: (Instant) -> Unit,
    onEndChange: (Instant) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.start_date),
            instant = start ?: Clock.System.now()
        ) {
            it?.let {
                onStartChange(fromEpochSeconds(it.toLong()))
            }
            showStartDatePicker = false
        }
    }

    if (showEndDatePicker) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.end_date),
            instant = end ?: Clock.System.now()
        ) {
            it?.let {
                onEndChange(fromEpochSeconds(it.toLong()))
            }
            showEndDatePicker = false
        }
    }

    AnimatedVisibility(!hidden) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(text = stringResource(Res.string.start_date))
                start?.takeIf { it.epochSeconds > 0 }
                    .let { startInstance ->
                        if (startInstance != null) {
                            FriendlyText(
                                modifier = Modifier.clickable {
                                    focusManager.clearFocus(true)
                                    showStartDatePicker = true
                                },
                                text = startInstance.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .toJavaLocalDateTime().format(formatter),
                                bold = true
                            )
                        }
                        FriendlyText(
                            modifier = Modifier.clickable {
                                if (startInstance == null) {
                                    focusManager.clearFocus(true)
                                    showStartDatePicker = true
                                } else {
                                    onStartChange(fromEpochSeconds(0))
                                }
                            },
                            text = if (startInstance == null)
                                stringResource(Res.string.add_start)
                            else
                                stringResource(Res.string.delete),
                            bold = true
                        )
                    }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(text = stringResource(Res.string.end_date))
                end?.takeIf { it.epochSeconds > 0 }
                    .let { endInstance ->
                        if (endInstance != null) {
                            FriendlyText(
                                modifier = Modifier.clickable {
                                    focusManager.clearFocus(true)
                                    showEndDatePicker = true
                                },
                                text = endInstance.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .toJavaLocalDateTime().format(formatter),
                                bold = true
                            )
                        }
                        FriendlyText(
                            modifier = Modifier.clickable {
                                if (endInstance == null) {
                                    focusManager.clearFocus(true)
                                    showEndDatePicker = true
                                } else {
                                    onEndChange(fromEpochSeconds(0))
                                }
                            },
                            text = if (endInstance == null)
                                stringResource(Res.string.add_end)
                            else
                                stringResource(Res.string.delete),
                            bold = true
                        )
                    }
            }
        }
    }
}