package com.neighbourly.app.c_business.usecase.ai

import com.neighbourly.app.d_entity.data.AiConversationMessage
import com.neighbourly.app.d_entity.data.AiVariant
import com.neighbourly.app.d_entity.interf.AI
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.interf.StatusUpdater

class AiChatUseCase(
    val sessionStore: SessionStore,
    val chatUpdater: StatusUpdater,
    val database: Db,
    val aiGw: AI
) {
    suspend fun execute(prompt: String) {
        chatUpdater.storeAiMessage(AiConversationMessage(text = prompt, inbound = false))
        aiGw.contentOverview(
            aiVariant = AiVariant.AiVariantGemini(apiKey = "AIzaSyB4Yix2w3QUpVKRLrI2Bfckd30XbGriYPg"),
            prompt = prompt,
            items = database.filterItems(),
            people = database.getUsers(),
            houses = database.filterHouseholds(),
            neighbourhoods = sessionStore.user?.neighbourhoods.orEmpty()
        )
    }
}