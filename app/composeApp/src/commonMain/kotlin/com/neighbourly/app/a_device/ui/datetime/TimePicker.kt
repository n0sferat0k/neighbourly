package com.neighbourly.app.a_device.ui.datetime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalTime

@Composable
fun TimePicker(
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedTime?.toString() ?: "",
        onValueChange = {},
        label = { Text("Select Time") },
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = { showDialog = false },
            onTimeSelected = {
                showDialog = false
                onTimeSelected(it)
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val times = (0..23).flatMap { hour ->
        listOf(0, 15, 30, 45).map { minute -> LocalTime(hour, minute) }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .padding(16.dp)
            ) {
                Text("Select a Time", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .padding(vertical = 8.dp)
                ) {
                    itemsIndexed(times) { index, time ->
                        Text(
                            text = time.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTimeSelected(time) }
                                .padding(8.dp)
                        )
                        if (index < times.lastIndex) Divider()
                    }
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}