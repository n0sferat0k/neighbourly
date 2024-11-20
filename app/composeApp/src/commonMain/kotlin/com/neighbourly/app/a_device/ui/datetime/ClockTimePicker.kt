package com.neighbourly.app.a_device.ui.datetime

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.utils.AppColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockTimePicker(
    modifier: Modifier = Modifier,
    instant: Instant,
    onChange: (Int, Int) -> Unit
) {
    val dateTime by derivedStateOf { instant.toLocalDateTime(TimeZone.currentSystemDefault()) }
    val state = rememberTimePickerState(
        initialHour = dateTime.hour,
        initialMinute = dateTime.minute,
        is24Hour = true
    )
    LaunchedEffect(dateTime)  {
        state.hour = dateTime.hour
        state.minute = dateTime.minute
    }
    LaunchedEffect(state.hour, state.minute) {
        onChange(state.hour, state.minute)
    }
    TimePicker(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        state = state,
        colors = TimePickerDefaults.colors(
            clockDialColor = AppColors.primaryLight,
            selectorColor = AppColors.primary,
            containerColor = AppColors.primaryLight,
            timeSelectorUnselectedContainerColor = AppColors.primaryLight,
            timeSelectorSelectedContainerColor = AppColors.primary
        ),
        layoutType = TimePickerLayoutType.Vertical
    )
}