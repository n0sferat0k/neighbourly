package com.neighbourly.app.a_device.ui.atomic.organism.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.chat.MessageBubble

@Composable
fun OrganismChatHistory(
    modifier: Modifier = Modifier,
    messages: List<String>,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = messages) { message ->
            MessageBubble(message)
        }
    }
}