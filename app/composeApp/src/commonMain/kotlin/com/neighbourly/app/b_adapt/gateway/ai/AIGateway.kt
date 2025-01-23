package com.neighbourly.app.b_adapt.gateway.ai

import com.neighbourly.app.a_device.api.KtorAI
import com.neighbourly.app.b_adapt.gateway.api.toHouseholdDTO
import com.neighbourly.app.b_adapt.gateway.api.toItemDTO
import com.neighbourly.app.b_adapt.gateway.api.toNeighbourhoodDTO
import com.neighbourly.app.b_adapt.gateway.api.toUserDTO
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.Neighbourhood
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.concurrent.TimeoutException

class AiGateway(
    val api: KtorAI,
) : AI {
    override suspend fun generate(
        items: List<Item>,
        people: List<User>,
        houses: List<Household>,
        neighbourhoods: List<Neighbourhood>
    ): String =
        runContextCatchTranslateThrow {
            api.generate(
                GenerateInput(
                    prompt = Json.encodeToString<AppContentDTO>(AppContentDTO(
                        items = items.map { it.toItemDTO() },
                        people = people.map { it.toUserDTO() },
                        houses = houses.map { it.toHouseholdDTO() },
                        neighbourhoods = neighbourhoods.map { it.toNeighbourhoodDTO() }
                    ))
                )
            ).response
        }

    suspend inline fun <R> runContextCatchTranslateThrow(crossinline block: suspend () -> R): R =
        runCatching {
            withContext(Dispatchers.IO) {
                block.invoke()
            }
        }.let {
            when {
                it.isSuccess -> {
                    it.getOrElse { throw OpException("Unknown Error") }
                }

                it.isFailure -> {
                    val messgae = it.exceptionOrNull().let {
                        when (it) {
                            is AiException -> it.msg
                            is IOException -> it.message ?: it.toString()
                            else -> "Unknown Error"
                        }
                    }

                    throw OpException(messgae)
                }

                else -> throw OpException("Unknown Error")
            }
        }
}


