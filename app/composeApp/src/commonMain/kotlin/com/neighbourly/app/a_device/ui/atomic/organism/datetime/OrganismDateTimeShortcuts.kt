package com.neighbourly.app.a_device.ui.atomic.organism.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.in_10_minutes
import neighbourly.composeapp.generated.resources.in_eight_hours
import neighbourly.composeapp.generated.resources.in_one_hour
import neighbourly.composeapp.generated.resources.in_two_hours
import neighbourly.composeapp.generated.resources.next_week
import neighbourly.composeapp.generated.resources.now
import neighbourly.composeapp.generated.resources.tomorrow
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganismDateTimeShortcuts(onSelectedInstant: (Instant) -> Unit) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FriendlyButton(text = stringResource(Res.string.now)) {
            onSelectedInstant(Clock.System.now())
        }
        FriendlyButton(text = stringResource(Res.string.in_10_minutes)) {
            onSelectedInstant(Clock.System.now().plus(10.minutes))
        }
        FriendlyButton(text = stringResource(Res.string.in_one_hour)) {
            onSelectedInstant(Clock.System.now().plus(1.hours))
        }
        FriendlyButton(text = stringResource(Res.string.in_two_hours)) {
            onSelectedInstant(Clock.System.now().plus(2.hours))
        }
        FriendlyButton(text = stringResource(Res.string.in_eight_hours)) {
            onSelectedInstant(Clock.System.now().plus(8.hours))
        }
        FriendlyButton(text = stringResource(Res.string.tomorrow)) {
            onSelectedInstant(Clock.System.now().plus(1.days).let {
                val timezone = TimeZone.currentSystemDefault()
                it.toLocalDateTime(timezone).date.atStartOfDayIn(timezone)
            })
        }
        FriendlyButton(text = stringResource(Res.string.next_week)) {
            onSelectedInstant(Clock.System.now().let {
                val timezone = TimeZone.currentSystemDefault()
                val date = it.toLocalDateTime(timezone).date
                val daysUntilNextMonday = 7 - date.dayOfWeek.ordinal
                date.plus(daysUntilNextMonday, DateTimeUnit.DAY)
                    .atStartOfDayIn(timezone)
            })
        }
    }
}