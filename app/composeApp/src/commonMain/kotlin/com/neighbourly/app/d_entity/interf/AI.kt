package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.Neighbourhood
import com.neighbourly.app.d_entity.data.User

interface AI {
    suspend fun generate(
        system: String,
        prompt: String
    ): String

    suspend fun contentOverview(
        items: List<Item>,
        people: List<User>,
        houses: List<Household>,
        neighbourhoods: List<Neighbourhood>
    ): String
}