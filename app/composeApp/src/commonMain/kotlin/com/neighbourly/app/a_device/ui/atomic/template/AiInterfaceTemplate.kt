package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.card.OkCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.chat.OrganismChatHistory
import com.neighbourly.app.a_device.ui.atomic.organism.chat.OrganismChatPromptInput
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.ai.AiInterfaceViewModel

@Composable
fun AiInterfaceTemplate(
    state: AiInterfaceViewModel.AiInterfaceViewState,
    onPrompt: (prompt: String) -> Unit,
    onClose: () -> Unit,
    onItemSelect: (itemId: Int) -> Unit,
    onHouseholdSelect: (itemId: Int) -> Unit
) {
    OrganismContentBubble(
        scrollable = false,
        content = {
            OrganismChatHistory(
                modifier = Modifier.fillMaxWidth().weight(1f),
                messages = state.aiMessages,
                onItemSelect = onItemSelect,
                onHouseholdSelect = onHouseholdSelect,
            )
            OrganismChatPromptInput(onPrompt = onPrompt)
        },
        footerContent = {
            OkCardFooter { onClose() }
        }
    )
}