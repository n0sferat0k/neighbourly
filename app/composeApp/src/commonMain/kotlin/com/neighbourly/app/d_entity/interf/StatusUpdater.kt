package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.AiConversationMessage

interface StatusUpdater {
    fun setOnline(isOnline: Boolean, isTokenExp: Boolean, lastError: String? = null)
    fun setAiOnline(isAiOnline: Boolean)
    fun storeAiMessage(message: AiConversationMessage)
}