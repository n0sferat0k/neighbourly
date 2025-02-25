package com.neighbourly.app.b_adapt.gateway.ai

import com.neighbourly.app.b_adapt.gateway.ai.bean.constructOverviewSystemPrompt
import kotlinx.serialization.Serializable

@Serializable
data class GenerateInputGemini(
    val systemInstruction: GeminiContent,
    val contents: List<GeminiContent> = emptyList()

)

@Serializable
data class GeminiContent(
    val parts: List<GeminiContentPart>
)

@Serializable
data class AIResponseGemini(
    val candidates: List<GeminiResponseCandidate>,
)

@Serializable
data class GeminiResponseCandidate(
    val content: GeminiResponseContent
)

@Serializable
data class GeminiResponseContent(
    val parts: List<GeminiResponseContentPart>
)

@Serializable
data class GeminiResponseContentPart(
    val text: String
)

@Serializable
data class GeminiContentPart(val text: String)

fun overviewInputGemini(jsonContext: String, prompt: String) = GenerateInputGemini(
    systemInstruction = GeminiContent(
        listOf(
            GeminiContentPart(
                constructOverviewSystemPrompt(
                    jsonContext
                )
            )
        )
    ),
    contents = listOf(GeminiContent(listOf(GeminiContentPart(prompt))))
)
