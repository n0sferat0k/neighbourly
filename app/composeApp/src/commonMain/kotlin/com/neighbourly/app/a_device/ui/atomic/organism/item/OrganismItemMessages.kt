package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.chat.MessageBubbleWithOrigin
import com.neighbourly.app.a_device.ui.atomic.organism.chat.OrganismChatPromptInput
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemMessageVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.messages
import org.jetbrains.compose.resources.stringResource


fun LazyListScope.OrganismItemMessages(
    messages: List<ItemMessageVS>,
    onPostMessage: (message: String) -> Unit,
    onDeleteMessage: (messageId: Int) -> Unit,
    onSelectHousehold: (householdId: Int) -> Unit,
) {
    item {
        HorizontalDivider(
            thickness = 2.dp,
            color = AppColors.primary
        )
    }
    item {
        FriendlyText(text = stringResource(Res.string.messages), bold = true)
    }
    items(items = messages, key = { it.id }) { message ->
        if (message.deletable) {
            SwipeToDeleteContainer(onDelete = { onDeleteMessage(message.id) }) {
                MessageBubbleWithOrigin(
                    modifier = Modifier.heightIn(min = 48.dp),
                    senderHouse = message.household,
                    sender = message.sender,
                    text = message.message,
                    onHouseholdClick = { message.household?.id?.let { onSelectHousehold(it) } }
                )
            }
        } else {
            MessageBubbleWithOrigin(
                senderHouse = message.household,
                sender = message.sender,
                text = message.message,
                onHouseholdClick = { message.household?.id?.let { onSelectHousehold(it) } }
            )
        }
    }
    item {
        OrganismChatPromptInput(onPrompt = onPostMessage)
    }
}