package com.neighbourly.app.a_device.ui.datetime

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.FriendlyText
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime


@Composable
fun CalendarDatePicker(modifier: Modifier, instant: Instant, onChange: (Int, Int, Int) -> Unit) {

    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val basisConfig = rememberMutableBasisEpicCalendarConfig()
    val state = rememberEpicDatePickerState(
        initialMonth = EpicMonth(dateTime.year, Month(dateTime.monthNumber)),
        selectedDates = listOf(LocalDate(dateTime.year, Month(dateTime.monthNumber), dateTime.dayOfMonth)),
        config = rememberEpicDatePickerConfig(
            pagerConfig = rememberEpicCalendarPagerConfig(
                basisConfig = basisConfig,
            ),
            selectionContentColor = AppColors.primary,
            selectionContainerColor = AppColors.primaryLight
        )
    )

    LaunchedEffect(state.selectedDates) {
        state.selectedDates.firstOrNull()?.let {
            onChange(it.year, it.month.number, it.dayOfMonth)
        }
    }

    Column(modifier = modifier) {
        FriendlyText(
            modifier.align(Alignment.CenterHorizontally),
            text = state.pagerState.currentMonth.month.name + " " + state.pagerState.currentMonth.year
        )
        EpicDatePicker(state = state)
    }
}