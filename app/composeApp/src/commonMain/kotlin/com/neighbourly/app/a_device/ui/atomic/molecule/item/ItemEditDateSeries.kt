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
import neighbourly.composeapp.generated.resources.add_date
import neighbourly.composeapp.generated.resources.dates
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.reminders
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@Composable
fun ItemEditDateSeries(
    hidden: Boolean = false,
    dates: List<Instant>,
    onChange: (List<Instant>) -> Unit
) {
    var showDatePickerInstant by remember { mutableStateOf<Instant?>(null) }
    var showDatePickerIndex by remember { mutableStateOf(-1) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    if (showDatePickerInstant != null) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.reminders),
            instant = showDatePickerInstant ?: Clock.System.now()
        ) {
            it?.let { ts ->
                val instant = fromEpochSeconds(ts.toLong())
                onChange(dates.toMutableList().apply {
                    if (showDatePickerIndex == -1) {
                        this += instant
                    } else {
                        this[showDatePickerIndex] = instant
                    }
                }.sorted())
            }
            showDatePickerInstant = null
        }
    }

    AnimatedVisibility(!hidden) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(text = stringResource(Res.string.dates))
                FriendlyText(modifier = Modifier.clickable {
                    showDatePickerIndex = -1
                    showDatePickerInstant = dates.lastOrNull() ?: Clock.System.now()
                }, text = stringResource(Res.string.add_date), bold = true)
            }

            dates.forEachIndexed { index, date ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FriendlyText(
                        modifier = Modifier.clickable {
                            showDatePickerIndex = index
                            showDatePickerInstant = date
                        },
                        text = date.toLocalDateTime(TimeZone.currentSystemDefault())
                            .toJavaLocalDateTime().format(formatter),
                        bold = true
                    )
                    FriendlyText(
                        modifier = Modifier.clickable {
                            onChange(dates.toMutableList().apply {
                                this.removeAt(index)
                            })
                        },
                        text = stringResource(Res.string.delete),
                        bold = true
                    )
                }
            }
        }
    }
}