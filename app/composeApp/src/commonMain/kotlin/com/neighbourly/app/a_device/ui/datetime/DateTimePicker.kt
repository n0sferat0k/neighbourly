package com.neighbourly.app.a_device.ui.datetime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(selectedDateTime?.date) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(selectedDateTime?.time) }

    Column(modifier = Modifier.padding(16.dp)) {
        DatePicker(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                if (selectedTime != null) {
                    onDateTimeSelected(LocalDateTime(it, selectedTime!!))
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimePicker(
            selectedTime = selectedTime,
            onTimeSelected = {
                selectedTime = it
                if (selectedDate != null) {
                    onDateTimeSelected(LocalDateTime(selectedDate!!, it))
                }
            }
        )
    }
}