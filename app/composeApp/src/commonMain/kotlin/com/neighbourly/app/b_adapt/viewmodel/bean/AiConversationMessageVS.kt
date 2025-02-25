package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.AiConversationMessage

data class AiConversationMessageVS(val text: String, val inbound: Boolean)

fun AiConversationMessage.toAiConversationMessageVS(): AiConversationMessageVS =
    AiConversationMessageVS(
        text = text,
        inbound = inbound
    )