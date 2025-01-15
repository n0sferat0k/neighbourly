package com.neighbourly.app.a_device.ui.atomic.organism.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardScrollableContent
import com.neighbourly.app.a_device.ui.atomic.molecule.card.OkCardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


@Composable
fun OrganismDateTimeDialog(title: String, instant: Instant, onTimestamp: (Int?) -> Unit) {
    var selectedInstant by remember { mutableStateOf(instant) }

    Dialog(
        onDismissRequest = { onTimestamp(null) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RoundedCornerCard(
                modifier = Modifier.align(Alignment.CenterStart)
                    .widthIn(max = 440.dp)
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CardHeader(Modifier.align(Alignment.Start), title = title)

                    CardScrollableContent(modifier = Modifier.weight(1f)) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OrganismDateTimeShortcuts {
                                selectedInstant = it
                            }

                            OrganismCalendarDatePicker(
                                Modifier.padding(4.dp),
                                selectedInstant
                            ) { year, month, day ->
                                val timezone = TimeZone.currentSystemDefault()
                                val localDateTime = selectedInstant.toLocalDateTime(timezone)

                                val adjustedDateTime = LocalDateTime(
                                    year = year,
                                    month = Month(month),
                                    dayOfMonth = day,
                                    hour = localDateTime.hour,
                                    minute = localDateTime.minute
                                )

                                // Convert back to Instant
                                selectedInstant = adjustedDateTime.toInstant(timezone)
                            }

                            OrganismClockTimePicker(
                                Modifier.padding(4.dp),
                                selectedInstant
                            ) { hour, minute ->
                                val timezone = TimeZone.currentSystemDefault()
                                val localDateTime = selectedInstant.toLocalDateTime(timezone)

                                val adjustedDateTime = LocalDateTime(
                                    year = localDateTime.year,
                                    month = localDateTime.month,
                                    dayOfMonth = localDateTime.dayOfMonth,
                                    hour = hour,
                                    minute = minute
                                )

                                // Convert back to Instant
                                selectedInstant = adjustedDateTime.toInstant(timezone)
                            }
                        }
                    }

                    OkCardFooter {
                        onTimestamp(selectedInstant.epochSeconds.toInt())
                    }
                }
            }
        }
    }
}