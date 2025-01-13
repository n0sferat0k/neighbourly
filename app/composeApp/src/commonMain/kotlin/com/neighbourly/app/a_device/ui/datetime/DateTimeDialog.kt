package com.neighbourly.app.a_device.ui.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.neighbourly.app.a_device.ui.utils.BoxFooter
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import com.neighbourly.app.a_device.ui.atomic.atom.RoundedCornerCard
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.organism.CalendarDatePicker
import com.neighbourly.app.a_device.ui.atomic.organism.ClockTimePicker
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.in_10_minutes
import neighbourly.composeapp.generated.resources.in_eight_hours
import neighbourly.composeapp.generated.resources.in_one_hour
import neighbourly.composeapp.generated.resources.in_two_hours
import neighbourly.composeapp.generated.resources.next_week
import neighbourly.composeapp.generated.resources.now
import neighbourly.composeapp.generated.resources.ok
import neighbourly.composeapp.generated.resources.tomorrow
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateTimeDialog(title: String, instant: Instant, onTimestamp: (Int?) -> Unit) {
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
                    BoxHeader(Modifier.align(Alignment.Start), title = title)

                    BoxScrollableContent(modifier = Modifier.weight(1f)) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FriendlyButton(text = stringResource(Res.string.now)) {
                                    selectedInstant = Clock.System.now()
                                }
                                FriendlyButton(text = stringResource(Res.string.in_10_minutes)) {
                                    selectedInstant = Clock.System.now().plus(10.minutes)
                                }
                                FriendlyButton(text = stringResource(Res.string.in_one_hour)) {
                                    selectedInstant = Clock.System.now().plus(1.hours)
                                }
                                FriendlyButton(text = stringResource(Res.string.in_two_hours)) {
                                    selectedInstant = Clock.System.now().plus(2.hours)
                                }
                                FriendlyButton(text = stringResource(Res.string.in_eight_hours)) {
                                    selectedInstant = Clock.System.now().plus(8.hours)
                                }
                                FriendlyButton(text = stringResource(Res.string.tomorrow)) {
                                    selectedInstant = Clock.System.now().plus(1.days).let {
                                        val timezone = TimeZone.currentSystemDefault()
                                        it.toLocalDateTime(timezone).date.atStartOfDayIn(timezone)
                                    }
                                }
                                FriendlyButton(text = stringResource(Res.string.next_week)) {
                                    selectedInstant = Clock.System.now().let {
                                        val timezone = TimeZone.currentSystemDefault()
                                        val date = it.toLocalDateTime(timezone).date
                                        val daysUntilNextMonday = 7 - date.dayOfWeek.ordinal
                                        date.plus(daysUntilNextMonday, DateTimeUnit.DAY)
                                            .atStartOfDayIn(timezone)
                                    }
                                }
                            }

                            CalendarDatePicker(
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

                            ClockTimePicker(
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

                    BoxFooter(modifier = Modifier.fillMaxWidth()) {
                        FriendlyButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            text = stringResource(Res.string.ok),
                        ) {
                            onTimestamp(selectedInstant.epochSeconds.toInt())
                        }
                    }
                }
            }
        }
    }
}