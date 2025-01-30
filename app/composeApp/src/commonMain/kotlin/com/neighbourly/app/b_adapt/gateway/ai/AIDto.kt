package com.neighbourly.app.b_adapt.gateway.ai

import com.neighbourly.app.b_adapt.gateway.api.HouseholdDTO
import com.neighbourly.app.b_adapt.gateway.api.ItemDTO
import com.neighbourly.app.b_adapt.gateway.api.NeighbourhoodDTO
import com.neighbourly.app.b_adapt.gateway.api.UserDTO
import kotlinx.serialization.Serializable

@Serializable
data class GenerateInput(
    val model: String = "deepseek-r1:14B",
    val stream: Boolean = false,
    val system: String,
    val prompt: String
)

fun overviewInput(jsonContext: String, prompt: String) = GenerateInput(
    system = """You are a summary generator for an android app made for rural and suburban neighbourhoods. 
        You will receive raw json information about people and households in the neighbourhood as well as items posted by the people.
        You will use the information to respond to user prompts, your answers should be short and concise.
        Here is some extra context that may be useful in doing your job:
        1. A user parent is someone who knows them personally and has added them to the neighbourhood, 
        2. Item types are: 
            INFO - just miscellaneous information, 
            DONATION - an item to be donated by the posting house, BARTER - an item to be traded for something else,
            SALE - an item to be sold for cash,
            EVENT - on event that is taking place, usually an open invitation,
            NEED - someone is in need of something, usually a thing,
            REQUEST - someone is requesting something, like a thing, some help, an acion to be taken and so on,
            SKILLSHARE - someone did or created something interesting or useful and they want to share their knowledge or experience,
            REMINDER - usually and important periodic thing that people should be reminded of, like trash day.
        3. items may have multiple dates, dates of creation or update but also start and end dates which signify the period in which the item is relevant.
        Here is the information json: $jsonContext
        """,
    prompt = prompt
)

@Serializable
data class AppContentDTO(
    val items: List<ItemDTO>,
    val people: List<UserDTO>,
    val houses: List<HouseholdDTO>,
    val neighbourhoods: List<NeighbourhoodDTO>
)

@Serializable
data class AIResponse(
    val model: String,
    val response: String,
)

class AiException(
    val msg: String,
) : RuntimeException(msg)