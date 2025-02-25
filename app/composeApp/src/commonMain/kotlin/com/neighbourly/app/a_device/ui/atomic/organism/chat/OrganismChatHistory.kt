package com.neighbourly.app.a_device.ui.atomic.organism.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.chat.MessageBubbleSimple
import com.neighbourly.app.b_adapt.viewmodel.bean.AiConversationMessageVS

@Composable
fun OrganismChatHistory(
    modifier: Modifier = Modifier,
    messages: List<AiConversationMessageVS>,
    onItemSelect: (itemId: Int) -> Unit,
    onHouseholdSelect: (itemId: Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = messages) { message ->
            if (message.inbound) {
                MessageBubbleSimple(
                    modifier = Modifier.padding(end = 20.dp),
                    text = message.text,
                    parse = true,
                    onItemSelect = onItemSelect,
                    onHouseholdSelect = onHouseholdSelect
                )
            } else {
                MessageBubbleSimple(modifier = Modifier.padding(start = 20.dp), text = message.text)
            }
        }
    }
}