package com.neighbourly.app.b_adapt.gateway.ai.bean

import com.neighbourly.app.b_adapt.gateway.api.HouseholdDTO
import com.neighbourly.app.b_adapt.gateway.api.ItemDTO
import com.neighbourly.app.b_adapt.gateway.api.NeighbourhoodDTO
import com.neighbourly.app.b_adapt.gateway.api.UserDTO
import kotlinx.serialization.Serializable

@Serializable
data class AppContentDTO(
    val items: List<ItemDTO>,
    val people: List<UserDTO>,
    val houses: List<HouseholdDTO>,
    val neighbourhoods: List<NeighbourhoodDTO>
)