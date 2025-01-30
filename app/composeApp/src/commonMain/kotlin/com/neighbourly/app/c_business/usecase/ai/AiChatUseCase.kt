package com.neighbourly.app.c_business.usecase.ai

import com.neighbourly.app.d_entity.interf.AI
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore

class AiChatUseCase(
    val sessionStore: SessionStore,
    val database: Db,
    val aiGw: AI
) {
    suspend fun execute(prompt: String) {
        database.getUsers()
        aiGw.contentOverview(
            prompt = prompt,
            items = database.filterItems(),
            people = database.getUsers(),
            houses = database.filterHouseholds(),
            neighbourhoods = sessionStore.user?.neighbourhoods.orEmpty()
        )
    }
}