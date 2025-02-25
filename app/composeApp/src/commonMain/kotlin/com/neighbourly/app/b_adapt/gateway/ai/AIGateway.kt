package com.neighbourly.app.b_adapt.gateway.ai

import com.neighbourly.app.a_device.api.KtorGeminiAI
import com.neighbourly.app.a_device.api.KtorOllamaAI
import com.neighbourly.app.b_adapt.gateway.ai.bean.AiException
import com.neighbourly.app.b_adapt.gateway.ai.bean.AppContentDTO
import com.neighbourly.app.b_adapt.gateway.api.toHouseholdDTO
import com.neighbourly.app.b_adapt.gateway.api.toItemDTO
import com.neighbourly.app.b_adapt.gateway.api.toNeighbourhoodDTO
import com.neighbourly.app.b_adapt.gateway.api.toUserDTO
import com.neighbourly.app.d_entity.data.AiConversationMessage
import com.neighbourly.app.d_entity.data.AiVariant
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.Neighbourhood
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AI
import com.neighbourly.app.d_entity.interf.StatusUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class AiGateway(
    val ollamaApi: KtorOllamaAI,
    val geminiApi: KtorGeminiAI,
    val statusUpdater: StatusUpdater,
) : AI {

    override suspend fun generate(
        aiVariant: AiVariant,
        system: String,
        prompt: String
    ): String =
        runContextCatchTranslateThrow {
            when (aiVariant) {
                is AiVariant.AiVariantGemini -> geminiApi.generate(
                    aiVariant.apiKey,
                    GenerateInputGemini(
                        systemInstruction = GeminiContent(listOf(GeminiContentPart(system))),
                        contents = listOf(GeminiContent(listOf(GeminiContentPart(prompt)))),
                    )
                ).candidates[0].content.parts[0].text

                is AiVariant.AiVariantOllama -> ollamaApi.generate(
                    GenerateInputOllama(system = system, prompt = prompt)
                ).response
            }
        }

    override suspend fun contentOverview(
        aiVariant: AiVariant,
        prompt: String,
        items: List<Item>,
        people: List<User>,
        houses: List<Household>,
        neighbourhoods: List<Neighbourhood>
    ): String =
        runContextCatchTranslateThrow {
            val jsonContext = Json.encodeToString<AppContentDTO>(AppContentDTO(
                items = items.map { it.toItemDTO() },
                people = people.map { it.toUserDTO() },
                houses = houses.map { it.toHouseholdDTO() },
                neighbourhoods = neighbourhoods.map { it.toNeighbourhoodDTO() }
            ))

            when (aiVariant) {
                is AiVariant.AiVariantGemini -> geminiApi.generate(
                    aiVariant.apiKey,
                    overviewInputGemini(jsonContext = jsonContext, prompt = prompt)
                ).candidates[0].content.parts[0].text

                is AiVariant.AiVariantOllama -> ollamaApi.generate(
                    overviewInputOllama(
                        jsonContext = jsonContext,
                        prompt = prompt
                    )
                ).response
            }
        }

    suspend inline fun <R> runContextCatchTranslateThrow(crossinline block: suspend () -> R): R =
        runCatching {
            withContext(Dispatchers.IO) {
                block.invoke()
            }
        }.let {
            when {
                it.isSuccess -> {
                    statusUpdater.setAiOnline(true)
                    it.getOrElse { throw OpException("Unknown Error") }.also { message ->
                        statusUpdater.storeAiMessage(
                            AiConversationMessage(
                                text = message.toString(),
                                inbound = true
                            )
                        )
                    }
                }

                it.isFailure -> {
                    statusUpdater.setAiOnline(false)
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


