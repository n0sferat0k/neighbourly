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
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn


@Composable
fun DatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.toString() ?: "",
        onValueChange = {},
        label = { Text("Select Date") },
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            onDateSelected = {
                showDialog = false
                onDateSelected(it)
            }
        )
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysRange = (0..365).map { today.plus(it, DateTimeUnit.DAY) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp)
            ) {
                Text("Select a Date", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .padding(vertical = 8.dp)
                ) {
                    itemsIndexed(daysRange) { index, date ->
                        Text(
                            text = date.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDateSelected(date) }
                                .padding(8.dp)
                        )
                        if (index < daysRange.lastIndex) Divider()
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