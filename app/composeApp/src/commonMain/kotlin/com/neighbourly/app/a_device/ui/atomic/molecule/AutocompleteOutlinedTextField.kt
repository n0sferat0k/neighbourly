package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AutocompleteOutlinedTextField(
    modifier: Modifier = Modifier, label: @Composable (() -> Unit)? = null,
    text: String = "",
    entries: Map<Int, String>,
    onSelect: (Int) -> Unit,
) {
    var filterText by remember { mutableStateOf(text) }
    var expanded by remember { mutableStateOf(false) }

    var filteredEntries by remember { mutableStateOf(entries.toList()) }

    LaunchedEffect(entries, filterText) {
        filteredEntries = entries.filter {
            it.value.lowercase().contains(filterText.lowercase())
        }.toList()
    }

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                expanded = false
            }
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                label = label,
                modifier = Modifier
                    .fillMaxWidth(),
                value = filterText,
                onValueChange = {
                    filterText = it
                    expanded = true
                },
                // Perform action when the TextField is clicked
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect { interaction ->
                                if (interaction is PressInteraction.Release) {
                                    expanded = !expanded
                                }
                            }
                        }
                    },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "arrow",
                        tint = Color.Black
                    )
                }
            )

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 160.dp),
                    ) {
                        items(filteredEntries) { (id, name) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        filterText = name
                                        expanded = false
                                        onSelect(id)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 15.dp)
                            ) {
                                Text(text = name, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}